/*
EduDB is made available under the OSI-approved MIT license.

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

package net.edudb.master.executor;

import net.edudb.engine.Utility;
import net.edudb.metadata_buffer.MetadataBuffer;
import net.edudb.workers_manager.WorkersManager;

import java.util.regex.Matcher;

/**
 * Connects all the registered workers
 *
 * @author Fady Sameh
 */
public class ConnectWorkersExecutor implements MasterExecutorChain {

    private MasterExecutorChain nextElement;

    @Override
    public void setNextElementInChain(MasterExecutorChain chainElement) {
        this.nextElement = chainElement;
    }

    private final String regex = "\\A(?:(?i)connect)\\s+(?:(?i)workers)\\s*;?\\z";

    @Override
    public void execute(String s) {
        if (s.toLowerCase().startsWith("connect workers")) {
            Matcher matcher = Utility.getMatcher(s, regex);
            if (matcher.matches()) {
                System.out.println("entered connect workers executor");
                for (String workerIdentifier : MetadataBuffer.getInstance().getWorkers().keySet()) {
                    String[] workerArgs = workerIdentifier.split(":");
                    String host = workerArgs[0];
                    int port = Integer.parseInt(workerArgs[1]);
                    System.out.println("attempting to connect to " + host + ":" + port);
                    WorkersManager.getInstance().connect(host, port);
                }
            }
        }
        else {
            nextElement.execute(s);
        }
    }
}
