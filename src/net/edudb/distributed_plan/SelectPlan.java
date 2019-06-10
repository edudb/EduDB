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
import net.edudb.distributed_operator.JoinOperator;
import net.edudb.distributed_operator.SelectOperator;
import net.edudb.distributed_operator.parameter.JoinOperatorParameter;
import net.edudb.distributed_operator.parameter.SelectOperatorParameter;
import net.edudb.distributed_query.QueryNode;
import net.edudb.distributed_query.QueryTree;
import net.edudb.exception.InvalidTypeValueException;
import net.edudb.join_request.JoinRequest;
import net.edudb.join_request.LocalJoinRequest;
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

        System.out.println(ra);
        /**
         * if the select is from a single table then it is fairly simple.
         * we only need to forward the command as is to the necessary shards.
         *
         * Joins are far far more complicated
         */
        if (ra.contains("EqJoin")) {
            String[] tableNames = translator.getTableNames(ra);

            if (tableNames == null || tableNames[0] == null || tableNames[1] == null) {
                MasterWriter.getInstance().write(new Response("A problem occurred"));
                return null;
            }

            Hashtable<String, DataType> leftTable = MetadataBuffer.getInstance().getTables().get(tableNames[0]);
            String leftDistributionMethod = leftTable.get("distribution_method").toString();

            Hashtable<String, DataType> rightTable = MetadataBuffer.getInstance().getTables().get(tableNames[1]);
            String rightDistributionMethod = rightTable.get("distribution_method").toString();

            if (leftDistributionMethod.equals("null") || rightDistributionMethod.equals("null")) {
                MasterWriter.getInstance().write("The distribution has not been set for all the tables");
                return null;
            }

            String leftTableMetaData = leftTable.get("metadata").toString();
            String rightTableMetaData = rightTable.get("metadata").toString();

            if (!translator.checkJoinConditionValidity(leftTableMetaData, rightTableMetaData, ra)) {
                MasterWriter.getInstance().write("The column types in the join condition do not match");
                return null;
            }

            Hashtable<String, Hashtable<String, DataType>> leftShards = new Hashtable<>();
            Hashtable<String, Hashtable<String, DataType>> rightShards = new Hashtable<>();

            ArrayList<JoinRequest> joinRequests = new ArrayList<>();

            if (leftDistributionMethod.equals("replication") && rightDistributionMethod.equals("replication")) {
                for (Hashtable<String, DataType> shard: MetadataBuffer.getInstance().getShards().values()) {

                    String shardTable = shard.get("table_name").toString();
                    String shardAddress = shard.get("host").toString() + shard.get("port").toString()
                            + shard.get("minimum_value") + shard.get("maximum_value");

                    if (shardTable.equals(leftTable.get("name").toString())) {
                        if (leftShards.get(shardAddress) == null) {
                            leftShards.put(shardAddress, shard);
                        }

                        if (rightShards.get(shardAddress) != null) {
                            joinRequests.add(new LocalJoinRequest(leftShards.get(shardAddress), rightShards.get(shardAddress)));
                            break;
                        }

                    }
                    else if (shardTable.equals(rightTable.get("name").toString())) {
                        if (rightShards.get(shardAddress) == null) {
                            rightShards.put(shardAddress, shard);
                        }

                        if (leftShards.get(shardAddress) != null) {
                            joinRequests.add(new LocalJoinRequest(leftShards.get(shardAddress), rightShards.get(shardAddress)));
                            break;
                        }
                    }
                }
            }


            if (joinRequests.size() >= 1) {
                JoinOperatorParameter parameter = new JoinOperatorParameter(statement, joinRequests);
                JoinOperator operator = new JoinOperator();
                operator.setParameter(parameter);

                QueryTree tree = new QueryTree((QueryNode)operator);

                return tree;
            }

            MasterWriter.getInstance().write(new Response("Currently only joins between replicated tables are supported."));
            return null;
        }
        else if (ra.contains("GenJoin")) {
            MasterWriter.getInstance().write(new Response("Currently only joins of the format 'SELECT [column list] "
            + "FROM table1 t1 INNER JOIN table2 t2 ON t.column = t2.column' are supported."));
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
                if (!shard.get("table_name").toString().equals(tableName))
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
