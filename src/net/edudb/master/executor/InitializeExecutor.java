/*
EduDB is made available under the OSI-approved MIT license.

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

package net.edudb.master.executor;

import net.edudb.engine.Utility;
import java.util.regex.Matcher;
import net.edudb.master.MasterWriter;
import net.edudb.meta_manager.MetaManager;
import net.edudb.response.Response;

/**
 * Handles the initialization of the connection with the client.
 *
 * @author FadySameh
 *
 */
public class InitializeExecutor implements MasterExecutorChain {
    private MasterExecutorChain nextElement;
    /**
     * Matches strings of the form: <br>
     * <br>
     * <b>[edudb::username::password]</b><br>
     * <br>
     * and captures <b>username</b> and <b>password</b> in the matcher's groups
     * one and two, respectively.
     */
    private String regex = "\\A\\[edudb\\:\\:(\\w+)\\:(\\w+)\\]\\z";

    @Override
    public void setNextElementInChain(MasterExecutorChain chainElement) {
        this.nextElement = chainElement;
    }

    @Override
    public void execute(String string) {
        if (string.toLowerCase().startsWith("[edudb::")) {
            Matcher matcher = Utility.getMatcher(string, regex);
            if (matcher.matches()) {
                /**
                 * Write anything to the client to initialize a connection with
                 * it.
                 *
                 */


                if (matcher.group(1).equals("admin") && matcher.group(2).equals("admin")) {

                    /**
                     * If master is not connected to meta database, connect
                     * to it
                     */
                    if (!MetaManager.getInstance().isConnected()) {
                        new Thread(MetaManager.getInstance()).start();
                    }
                    MasterWriter.getInstance().write(new Response("[edudb::init]"));
                } else {
                    MasterWriter.getInstance().write(new Response("[edudb::mismatch]"));
                }
            }
        }
        else
        nextElement.execute(string);
    }
}
