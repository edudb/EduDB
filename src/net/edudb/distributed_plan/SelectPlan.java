/*
EduDB is made available under the OSI-approved MIT license.

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

package net.edudb.distributed_plan;

import net.edudb.condition.Condition;
import net.edudb.condition.NullCondition;
import net.edudb.data_type.DataType;
import net.edudb.data_type.DataTypeFactory;
import net.edudb.distributed_operator.SelectOperator;
import net.edudb.distributed_operator.parameter.SelectOperatorParameter;
import net.edudb.distributed_query.QueryNode;
import net.edudb.distributed_query.QueryTree;
import net.edudb.exception.InvalidTypeValueException;
import net.edudb.master.MasterWriter;
import net.edudb.metadata_buffer.MetadataBuffer;
import net.edudb.response.Response;
import net.edudb.statement.SQLSelectStatement;
import net.edudb.statement.SQLStatement;
import net.edudb.translator.Translator;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Random;

/**
 * A plan to select data from worker database
 *
 * @author Fady Sameh
 */
public class SelectPlan extends DistributedPlan {

    public QueryTree makePlan(SQLStatement sqlStatement) {
        SQLSelectStatement statement = (SQLSelectStatement)sqlStatement;

        Translator translator = new Translator();
        String ra = translator.translate(statement.toString());

        if (ra == null)
            return null;

        /**
         * if the select is from a single table then it is far more simple.
         * we only need to forward the command as is to the necessary shard.
         *
         * Joins are far far more complicated
         */
        if (ra.contains("EqJoin")) {
            MasterWriter.getInstance().write(new Response("Joins are not yet supported :("));
            return null;
        }
        else {
            String tableName = translator.getTableName(ra);


//            if (tableName != null)
//                MasterWriter.getInstance().write(new Response(tableName));
            //System.out.println("Table name: " + tableName);
            Hashtable<String, DataType> table = MetadataBuffer.getInstance().getTables().get(tableName);

            String distributionColumn = table.get("distribution_column").toString();

            String metadata = table.get("metadata").toString();

            String[] metadataArray = metadata.split(" ");

            String distributionColumnType = "";

            for (int i = 0; i < metadataArray.length; i+=2) {
                if (distributionColumn.equals(metadataArray[i]))
                    distributionColumnType = metadataArray[i+1];
            }

            ArrayList<Condition> distributionConditions = new ArrayList<>();
            if (ra.contains("Filter")) {
                distributionConditions = translator.getDistributionCondition(metadata, distributionColumn, ra);
            }
            else {
                distributionConditions.add(new NullCondition());
            }

            if (distributionConditions == null)
                return null;

            DataTypeFactory dataTypeFactory = new DataTypeFactory();
            Hashtable<String, ArrayList<Hashtable<String, DataType>>> shards  = new Hashtable<>();

            for (Hashtable<String, DataType> shard: MetadataBuffer.getInstance().getShards().values()) {
                if (!shard.get("table").toString().equals(tableName))
                    continue;

                if (table.get("distribution_method").toString().equals("sharding")) {
                    DataType shardMinValue = null;
                    DataType shardMaxValue = null;
                    try {
                        shardMinValue = dataTypeFactory.makeType(distributionColumnType,
                                shard.get("min_value").toString());
                        shardMaxValue = dataTypeFactory.makeType(distributionColumnType,
                                shard.get("max_value").toString());
                    } catch (InvalidTypeValueException e) {
                        System.out.println(e.getMessage());
                        return null;
                    }

                    boolean validShard = false;

                    for (Condition condition: distributionConditions)
                        if (condition.evaluate(shardMinValue, shardMaxValue)) {
                            validShard = true;
                            break;
                        }

                    if (!validShard) continue;
                }

                String id = shard.get("id").toString();

                if (shards.get(id) == null) {
                    shards.put(id, new ArrayList<>());
                }

                shards.get(id).add(shard);
            }

            /**
             * We don't need to forward the command to all the shards; only one shard from
             * each id. We're going to select the shards randomly for load balancing.
             */

            ArrayList<Hashtable<String, DataType>> finalShards  = new ArrayList<>(); // shards we should forward the command to

            for (ArrayList<Hashtable<String, DataType>> shardGroup: shards.values()) {
                int groupSize = shardGroup.size();
                int randomIndex = new Random().nextInt(groupSize);
                finalShards.add(shardGroup.get(randomIndex));
            }

            SelectOperator operator = new SelectOperator();

            SelectOperatorParameter parameter = new SelectOperatorParameter(statement, finalShards, tableName);

            operator.setParameter(parameter);

            QueryTree tree = new QueryTree((QueryNode)operator);

            return tree;
        }

    }

}
