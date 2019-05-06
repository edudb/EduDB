/*
EduDB is made available under the OSI-approved MIT license.

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

package net.edudb.distributed_plan;

import net.edudb.data_type.DataType;
import net.edudb.data_type.DataTypeFactory;
import net.edudb.distributed_operator.InsertOperator;
import net.edudb.distributed_operator.parameter.InsertOperatorParamater;
import net.edudb.distributed_query.QueryNode;
import net.edudb.distributed_query.QueryTree;
import net.edudb.exception.InvalidTypeValueException;
import net.edudb.master.MasterWriter;
import net.edudb.metadata_buffer.MetadataBuffer;
import net.edudb.response.Response;
import net.edudb.statement.SQLInsertStatement;
import net.edudb.statement.SQLStatement;

import java.util.ArrayList;
import java.util.Hashtable;

/**
 * A plan to insert data into shard tables in worker
 * databases
 *
 * @author Fady Sameh
 */
public class InsertPlan extends DistributedPlan {

    public QueryTree makePlan(SQLStatement sqlStatement) {
        SQLInsertStatement statement = (SQLInsertStatement) sqlStatement;
        String tableName = statement.getTableName();

        Hashtable<String, DataType> table = MetadataBuffer.getInstance().getTables().get(tableName);

        if (table == null) {
            MasterWriter.getInstance().write(new Response("Table '" + table + "' does not exist"));
            return null;
        }

        String distributionMethod = table.get("distribution_method").toString();

        if (distributionMethod.equals("null")) {
            MasterWriter.getInstance().write(new Response("You must set the distribution method for this table first"));
            return null;
        }

        DataTypeFactory dataTypeFactory = new DataTypeFactory();

        String distributionColumn = table.get("distribution_column").toString();

        String tableMetadata = table.get("metadata").toString();

        String distributionColumnType = "";

        DataType distributionColumnValue = null;

        ArrayList<String> values = statement.getValueList();

        String[] metadataArray = tableMetadata.split(" ");

        if (values.size() != metadataArray.length / 2) {
            MasterWriter.getInstance().write(new Response("Expected " + metadataArray.length / 2 + " values" +
                    " but received " + values.size()));
            return null;
        }

        for (int i = 0; i < metadataArray.length; i+=2) {
            DataType value = null;
            try {
                value = dataTypeFactory.makeType(metadataArray[i+1], values.get(i/2));
            } catch (InvalidTypeValueException e) {
                MasterWriter.getInstance().write(new Response(e.getMessage()));
                return null;
            }
            if (metadataArray[i].equals(distributionColumn)) {
                distributionColumnType = metadataArray[i + 1];
                distributionColumnValue = value;
                break;
            }
        }

        /**
         * We need to get all the shards that the new record should
         * be inserted into
         */
        ArrayList<Hashtable<String, DataType>> shards  = new ArrayList<>(); // shards to insert into

        for (Hashtable<String, DataType> shard: MetadataBuffer.getInstance().getShards().values()) {
            if (!shard.get("table_name").toString().equals(tableName))
                continue;

            if (distributionMethod.equals("sharding")) {
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

                /** comparing new value with shard's minimum and maximum value */

                if (distributionColumnValue.compareTo(shardMinValue) < 0
                        || distributionColumnValue.compareTo(shardMaxValue) > 0)
                    continue;

            }

            shards.add(shard);
        }

        if (shards.size() == 0) {
            MasterWriter.getInstance().write(new Response("Did not find a suitable shard to insert this record"));
            return null;
        }

        InsertOperator operator = new InsertOperator();
        InsertOperatorParamater parameter = new InsertOperatorParamater(statement, shards);
        operator.setParameter(parameter);

        QueryTree tree = new QueryTree((QueryNode)operator);

        return tree;

    }
}
