/*
EduDB is made available under the OSI-approved MIT license.

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

package net.edudb.master.executor;

import net.edudb.engine.Utility;
import net.edudb.master.MasterWriter;
import net.edudb.response.Response;
import net.edudb.workers_manager.WorkersManager;

import java.util.regex.Matcher;

/**
 * Connects a worker node and registers it in the metadata if it
 * hasn't been registered already.
 *
 * @author Fady Sameh
 */
public class ConnectWorkerExecutor implements MasterExecutorChain {

    private MasterExecutorChain nextElement;
    private final String regex = "\\A(?:(?i)connect)\\s+(?:(?i)worker)\\s+\\((\\w+), (\\w+)\\)\\s*;?\\z";

    @Override
    public void setNextElementInChain(MasterExecutorChain chainElement) {
        this.nextElement = chainElement;
    }

    @Override
    public void execute(String s) {
        if (s.toLowerCase().startsWith("connect worker ")) {
            Matcher matcher = Utility.getMatcher(s, regex);
            if (matcher.matches()) {
                String host = matcher.group(1);
                int port = 0;
                try {
                    port = Integer.parseInt(matcher.group(2));
                } catch (NumberFormatException e) {
                    MasterWriter.getInstance().write(new Response("'" + matcher.group(2) + "' is not a valid port"));
                    return;
                }
                //MasterWriter.getInstance().write(new Response("Trying to conncect to " + host + ":" + port));
                WorkersManager.getInstance().connect(host, port);
            }
        }
         else {
            nextElement.execute(s);
        }
    }
}
