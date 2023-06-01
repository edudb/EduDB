/*
 *
 * EduDB is made available under the OSI-approved MIT license.
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 * /
 */

package net.edudb.master.executor;

import net.edudb.Response;
import net.edudb.data_type.DataType;
import net.edudb.data_type.DataTypeFactory;
import net.edudb.data_type.IntegerType;
import net.edudb.data_type.VarCharType;
import net.edudb.engine.Utility;
import net.edudb.exception.InvalidTypeValueException;
import net.edudb.master.MasterWriter;
import net.edudb.meta_manager.MetaDAO;
import net.edudb.meta_manager.MetaManager;
import net.edudb.metadata_buffer.MetadataBuffer;
import net.edudb.structure.Record;
import net.edudb.worker_manager.WorkerDAO;
import net.edudb.workers_manager.WorkersManager;

import java.util.Hashtable;
import java.util.regex.Matcher;

/**
 * Creates a shard of a table (whether sharded or replicated)
 * and creates the shard table in the worker
 */
public class CreateShardExecutor implements MasterExecutorChain {

    private MasterExecutorChain nextElement;
    private final String regex = "\\A(?:(?i)create)\\s+(?:(?i)shard)\\s+\\((\\w+), (\\w+), (\\w+), (\\w+), (\\w+)\\)\\s*;?\\z";

    @Override
    public void setNextElementInChain(MasterExecutorChain chainElement) {
        this.nextElement = chainElement;
    }

    public void execute(String string) {
        if (string.toLowerCase().startsWith("create shard")) {
            Matcher matcher = Utility.getMatcher(string, regex);
            if (matcher.matches()) {
                String tableName = matcher.group(1);
                String workerHost = matcher.group(2);
                int workerPort = Integer.parseInt(matcher.group(3));
                String minValue = matcher.group(4);
                String maxValue = matcher.group(5);

                Hashtable<String, DataType> table = MetadataBuffer.getInstance().getTables().get(tableName);

                if (table == null) {
                    MasterWriter.getInstance().write(new Response("Table '" + tableName + "' does not exist"));
                    return;
                }

                Record worker = MetadataBuffer.getInstance().getWorkers().get(workerHost + ":" + workerPort);

                if (worker == null) {
                    MasterWriter.getInstance().write(new Response(
                            "No worker registered at " + workerHost + ":" + workerPort
                    ));
                    return;
                }

                WorkerDAO workerDAO = WorkersManager.getInstance().getWorkers().get(workerHost + ":" + workerPort);

                if (workerDAO == null) {
                    MasterWriter.getInstance().write(new Response("Worker at " + workerHost + ":" + workerPort + " is not currently online."));
                    return;
                }

                String distributionMethod = ((VarCharType) table.get("distribution_method")).getString();
                MetaDAO metaDAO = MetaManager.getInstance();

                switch (distributionMethod) {
                    case "null":
                        MasterWriter.getInstance().write(new Response("Distribution method for table '" + tableName +
                                "' has not been set"));
                        return;
                    case "replication":
                        if (MetadataBuffer.getInstance().getShards().get(workerHost + ":" + workerPort + ":" + tableName
                                + ":null") != null) {
                            MasterWriter.getInstance().write(new Response("Duplicate shard exists at " + workerHost
                                    + ":" + workerPort));
                            return;
                        }


                        metaDAO.writeShard(workerHost, workerPort, tableName, 0, "null", "null");
                        workerDAO.createTable(tableName, table.get("metadata").toString());
                        return;
                    case "sharding":

                        String distributionColumn = ((VarCharType) table.get("distribution_column")).getString();
                        String metadata = ((VarCharType) table.get("metadata")).getString();

                        String[] metadataArray = metadata.split(" ");
                        String distributionColumnType = "";
                        for (int i = 0; i < metadataArray.length; i += 2)
                            if (metadataArray[i].equals(distributionColumn)) {
                                distributionColumnType = metadataArray[i + 1].toLowerCase();
                                break;
                            }

                        /**
                         * used to generate DataType objects to compare supplied min and max
                         * shard values with the existing shards
                         */
                        DataTypeFactory dataTypeFactory = new DataTypeFactory();

                        DataType minimumShardValue = null;
                        DataType maximumShardValue = null;
                        try {
                            minimumShardValue = dataTypeFactory.makeType(distributionColumnType, minValue);
                            maximumShardValue = dataTypeFactory.makeType(distributionColumnType, maxValue);
                        } catch (InvalidTypeValueException e) {
                            e.printStackTrace();
                            MasterWriter.getInstance().write(new Response(e.getMessage()));
                            return;
                        }

                        if (minimumShardValue.compareTo(maximumShardValue) > 0) {
                            MasterWriter.getInstance().write(new Response("Minimum shard value must be smaller than maximum shard value"));
                            return;
                        }

                        Hashtable<String, Hashtable<String, DataType>> shards = MetadataBuffer.getInstance().getShards();

                        /**
                         * making sure that new shard does not overlap with an existing shard
                         */
                        for (Hashtable<String, DataType> shard : shards.values()) {
                            if (((VarCharType) shard.get("table_name")).getString().equals(tableName)) {

                                DataType currentShardMin = null;
                                DataType currentShardMax = null;

                                try {
                                    currentShardMin = dataTypeFactory.makeType(distributionColumnType,
                                            shard.get("min_value").toString());
                                    currentShardMax = dataTypeFactory.makeType(distributionColumnType,
                                            shard.get("max_value").toString());
                                } catch (InvalidTypeValueException e) {

                                }

                                /**
                                 * if new minimum is smaller than an existing shard's maximum and new maximum is
                                 * bigger than the same shard's minimum
                                 */
                                if (minimumShardValue.compareTo(currentShardMax) <= 0
                                        && maximumShardValue.compareTo(currentShardMin) >= 0) {
                                    MasterWriter.getInstance().write(new Response("Values overlap wih existing shards"));
                                    return;
                                }

                            }
                        }

                        int shardId = ((IntegerType) table.get("shard_number")).getInteger() + 1;

                        metaDAO.writeShard(workerHost, workerPort, tableName, shardId, minValue, maxValue);
                        metaDAO.editTable(tableName, null, null, shardId);
                        workerDAO.createTable(tableName + shardId, table.get("metadata").toString());
                }

            }
        } else {
            nextElement.execute(string);
        }

    }

}
