/*
EduDB is made available under the OSI-approved MIT license.

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

package net.edudb.master.executor;

import net.edudb.console.executor.ConsoleExecutorChain;
import net.edudb.engine.Utility;
import net.edudb.master.MasterWriter;
import net.edudb.meta_manager.MetaDAO;
import net.edudb.meta_manager.MetaManager;
import net.edudb.metadata_buffer.MetadataBuffer;
import net.edudb.response.Response;
import net.edudb.structure.Record;
import net.edudb.worker_manager.WorkerManager;
import net.edudb.workers_manager.WorkersManager;

import java.util.Hashtable;
import java.util.regex.Matcher;

/**
 * Executes a DROP DATABASE statement
 * this includes:
 * - Dropping the database in the metadata server
 * - Dropping the database in all the registered workers of this database
 *
 * @author Fady Sameh
 */
public class DropDatabaseExecutor implements MasterExecutorChain {

    private MasterExecutorChain nextElement;
    private String regex = "\\A(?:(?i)drop)\\s+(?:(?i)database)\\s+(\\D\\w*)\\s*;?\\z";

    @Override
    public void setNextElementInChain(MasterExecutorChain chainElement) {
        this.nextElement = chainElement;
    }

    public void execute(String string) {
        if (string.toLowerCase().startsWith("drop database")) {
            Matcher matcher = Utility.getMatcher(string, regex);
            if (matcher.matches()) {
                String databaseName = matcher.group(1);
               if (!databaseName.equals(MetaManager.getInstance().getDatabaseName())) {
                   MasterWriter.getInstance().write(new Response("Please open this database first"));
                   return;
               }

                Hashtable<String, Record> workers = MetadataBuffer.getInstance().getWorkers();

               Response[] responses = new Response[workers.size()];
               Object[] workerUrls = workers.keySet().toArray();
               for (int i = 0; i < workerUrls.length; i++) {
                   String workerUrl = (String) workerUrls[i];
                   WorkerManager workerManager = WorkersManager.getInstance().getWorkers().get(workerUrl);

                   if (workerManager == null) {
                       MasterWriter.getInstance().write(new Response("Worker at '" + workerUrl + "' is not connected"));
                       return;
                   }

                   final int index = i;
                   new Thread(
                           () -> responses[index] = workerManager.dropDatabase(databaseName)
                   ).start();
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

                WorkersManager.getInstance().getWorkers().clear();

                MetaManager.getInstance().dropDatabase(databaseName);

                MetadataBuffer.getInstance().clearAll();

                MasterWriter.getInstance().write(new Response("Database '" + databaseName + "' dropped successfully"));
            }
        }
        else {
            nextElement.execute(string);
        }
    }
}
