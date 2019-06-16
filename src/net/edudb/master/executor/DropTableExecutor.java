/*
EduDB is made available under the OSI-approved MIT license.

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

package net.edudb.master.executor;

import net.edudb.data_type.DataType;
import net.edudb.data_type.IntegerType;
import net.edudb.engine.Utility;
import net.edudb.master.MasterWriter;
import net.edudb.meta_manager.MetaDAO;
import net.edudb.meta_manager.MetaManager;
import net.edudb.metadata_buffer.MetadataBuffer;
import net.edudb.response.Response;
import net.edudb.worker_manager.WorkerDAO;
import net.edudb.workers_manager.WorkersManager;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.regex.Matcher;

/**
 * Executes a DROP TABLE statement
 * this includes:
 * - Deleting all the shard tables from the workers
 * - Delete all the information about the shards from the metadata database
 * - Delete information about the table from the metadata database
 *
 * @author Fady Sameh
 */
public class DropTableExecutor implements MasterExecutorChain{

    private MasterExecutorChain nextElement;
    private String regex = "\\A(?:(?i)drop)\\s+(?:(?i)table)\\s+(\\D\\w*)\\s*;?\\z";

    @Override
    public void setNextElementInChain(MasterExecutorChain chainElement) {
        this.nextElement = chainElement;
    }

    public void execute(String string) {
        if (string.toLowerCase().startsWith("drop table")) {
            Matcher matcher = Utility.getMatcher(string, regex);
            if (matcher.matches()) {
                String tableName = matcher.group(1);
                if (MetadataBuffer.getInstance().getTables().get(tableName) == null) {
                    MasterWriter.getInstance().write(new Response("Table '" + tableName + "' does not exist"));
                    return;
                }

                ArrayList<Hashtable<String, DataType>> shards = new ArrayList<>();
                for (Hashtable<String, DataType> shard: MetadataBuffer.getInstance().getShards().values()) {
                    if (shard.get("table_name").toString().equals(tableName)) {
                        shards.add(shard);
                    }
                }

                Response[] responses = new Response[shards.size()];

                for (int i = 0; i < responses.length; i++) {
                    Hashtable<String, DataType> shard = shards.get(i);
                    String workerAddress = shard.get("host").toString() + ":" + shard.get("port").toString();
                    WorkerDAO workerDAO = WorkersManager.getInstance().getWorkers().get(workerAddress);

                    if (workerDAO == null) {
                        MasterWriter.getInstance().write(new Response("Worker at '" + workerAddress + "' is not available"));
                        return;
                    }

                    int id = ((IntegerType)shard.get("id")).getInteger();
                    String dropTableStatement = string;

                    if (id > 0)
                        dropTableStatement = dropTableStatement.replace(tableName, tableName + id);

                    final int index = i;
                    final String finalDropTableStatement = dropTableStatement;

                    new Thread(() -> responses[index] = workerDAO.insert(finalDropTableStatement)).start();
                }

                int index = 0;
                int responsesReceived = 0;

                while (responsesReceived != responses.length) {
                    if (responses[index] == null)
                        responsesReceived = 0;
                    else
                        ++responsesReceived;

                    index = (index + 1) % responses.length;
                }

                /**
                 * Deleting shards' information
                 */
                MetaDAO metaDAO = MetaManager.getInstance();

                metaDAO.deleteShards(tableName);

                /**
                 * Deleting table's information
                 */
                metaDAO.deleteTable(tableName);

                MetadataBuffer.getInstance().clearAll();

                MasterWriter.getInstance().write(new Response("Table '" + tableName + "' deleted successfully"));
            }


        }
        else {
            nextElement.execute(string);
        }
    }

}
