/*
EduDB is made available under the OSI-approved MIT license.

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

package net.edudb.metadata_buffer;

import net.edudb.data_type.DataType;
import net.edudb.data_type.IntegerType;
import net.edudb.data_type.VarCharType;
import net.edudb.meta_manager.MetaDAO;
import net.edudb.meta_manager.MetaManager;
import net.edudb.structure.Column;
import net.edudb.structure.Record;

import java.util.ArrayList;
import java.util.Hashtable;

/**
 * This buffer is used to store results received from the
 * metadata database in the main memory
 *
 * @author Fady Sameh
 */
public class MetadataBuffer {

    private static final MetadataBuffer instance = new MetadataBuffer();

    private final Hashtable<String, Hashtable<String, DataType>> tables = new Hashtable();
    private final Hashtable<String, Record> workers = new Hashtable();
    private final Hashtable<String, Hashtable<String, DataType>> shards = new Hashtable<>();

    private MetadataBuffer () {}

    public static MetadataBuffer getInstance() { return instance; }

    public Hashtable<String, Hashtable<String, DataType>> getTables() {
        if (tables.isEmpty()) {
            MetaDAO metaDAO = MetaManager.getInstance();
            ArrayList<Record> tableRecords = metaDAO.getAll("tables");
            //MasterWriter.getInstance().write(new Response("relation", tableRecords, null));
            if (tableRecords != null) {
                for (Record table: tableRecords) {
                    String tableName = "";
                    Hashtable <String, DataType> tableData = new Hashtable<>();
                    for (Column column: table.getData().keySet()) {
                        if ((column.toString()).equals("name")) {
                            tableName = ((VarCharType)table.getData().get(column)).getString();
                        }
                        tableData.put(column.toString(), table.getData().get(column));
                    }

                    tables.put(tableName, tableData);
                }
            }
        }
        return tables;
    }

    public Hashtable<String, Record> getWorkers() {
        if (workers.isEmpty()) {
            MetaDAO metaDAO = MetaManager.getInstance();
            ArrayList<Record> workerRecords = metaDAO.getAll("workers");

            if (workerRecords != null) {
                for (Record worker: workerRecords) {
                    String workerAddress = ":";

                    for (Column column: worker.getData().keySet()) {

                        if ((column.toString()).equals("host")) {
                            String host = ((VarCharType)worker.getData().get(column)).getString();
                            workerAddress = host + workerAddress;
                        }

                        if ((column.toString()).equals("port")) {
                            String port = ((IntegerType)worker.getData().get(column)).getInteger() + "";
                            workerAddress += port;
                        }


                    }

                    workers.put(workerAddress, worker);

                }
            }
        }
        return workers;
    }

    public Hashtable<String, Hashtable<String, DataType>> getShards() {
        if (shards.isEmpty()) {
            MetaDAO metaDAO = MetaManager.getInstance();
            ArrayList<Record> shardRecords = metaDAO.getAll("shards");

            if (shardRecords != null) {
                for (Record shard: shardRecords) {

                    String workerAddress = ":";
                    String tableName = "";
                    String minValue = "";

                    Hashtable<String, DataType> shardData = new Hashtable<>();

                    for (Column column: shard.getData().keySet()) {

                        if ((column.toString()).equals("host")) {
                            String host = ((VarCharType)shard.getData().get(column)).getString();
                            workerAddress = host + workerAddress;
                        }

                        if ((column.toString()).equals("port")) {
                            String port = ((IntegerType)shard.getData().get(column)).getInteger() + "";
                            workerAddress += port;
                        }

                        if ((column.toString()).equals("table_name")) {
                            tableName = ((VarCharType)shard.getData().get(column)).getString();
                        }

                        if ((column.toString()).equals("min_value")) {
                            minValue = ((VarCharType)shard.getData().get(column)).getString();
                        }

                        shardData.put(column.toString(), shard.getData().get(column));

                    }

                    shards.put(workerAddress + ":" + tableName + ":" + minValue, shardData);

                }
            }
        }
        return shards;
    }

    public void clearAll() {
        workers.clear();
        shards.clear();
        tables.clear();
        System.out.println("all is cleared");
        System.out.println(workers.isEmpty());
        System.out.println(tables.isEmpty());
        System.out.println(shards.isEmpty());
    }
}
