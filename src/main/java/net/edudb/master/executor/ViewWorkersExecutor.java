/*
EduDB is made available under the OSI-approved MIT license.

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

package net.edudb.master.executor;

import net.edudb.engine.Utility;
import net.edudb.master.MasterWriter;
import net.edudb.metadata_buffer.MetadataBuffer;
import net.edudb.response.Response;
import net.edudb.structure.Record;
import net.edudb.worker_manager.WorkerManager;
import net.edudb.workers_manager.WorkersManager;

import java.util.Hashtable;
import java.util.regex.Matcher;

/**
 * Displays a list of registered workers and their connection status
 *
 * @author Fady Sameh
 */

public class ViewWorkersExecutor implements MasterExecutorChain {
    private MasterExecutorChain nextElement;

    private String regex = "\\A(?:(?i)show)\\s+(?:(?i)workers)\\s*;?\\z";


    @Override
    public void setNextElementInChain(MasterExecutorChain chainElement) {
        this.nextElement = chainElement;
    }

    @Override
    public void execute(String s) {
        if (s.toLowerCase().startsWith("show workers")) {
            Matcher matcher = Utility.getMatcher(s, regex);
            if (matcher.matches()) {
                Hashtable<String, Record> workersData = MetadataBuffer.getInstance().getWorkers();
                if (workersData.isEmpty()) {
                    MasterWriter.getInstance().write(new Response("No workers registered in this database"));
                    return;
                }
                Hashtable<String, WorkerManager> connectedWorkers = WorkersManager.getInstance().getWorkers();

                String workersTable =
                        "-------------------------------------------------" + "\r\n" +
                                "| ADDRESS                   | STATUS            |" + "\r\n" +
                                "-------------------------------------------------" + "\r\n";

                for (String address : workersData.keySet()) {
                    String status = (connectedWorkers.get(address) == null) ? "offline" : "online";
                    workersTable += "| " + address;
                    for (int i = 0; i < 26 - address.length(); i++) workersTable += " ";
                    workersTable += "| " + status;
                    for (int i = 0; i < 18 - status.length(); i++) workersTable += " ";
                    workersTable += "|\r\n";
                    workersTable += "-------------------------------------------------\r\n";

                }

                MasterWriter.getInstance().write(new Response(workersTable));
            }
        }
        else {
            nextElement.execute(s);
        }
    }
}
