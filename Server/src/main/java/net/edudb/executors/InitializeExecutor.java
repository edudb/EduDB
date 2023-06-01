/*
 *
 * EduDB is made available under the OSI-approved MIT license.
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 * /
 */

package net.edudb.executors;

import net.edudb.Request;
import net.edudb.Response;
import net.edudb.engine.Utility;

import java.util.regex.Matcher;

/**
 * Handles the initialization of the connection with the client.
 *
 * @author Ahmed Abdul Badie
 */
public class InitializeExecutor implements ConsoleExecutorChain {
    private ConsoleExecutorChain nextElement;
    /**
     * Matches strings of the form: <br>
     * <br>
     * <b>[edudb::username::password]</b><br>
     * <br>
     * and captures <b>username</b> and <b>password</b> in the matcher's groups
     * one and two, respectively.
     */
    private final static String regex = "\\A\\[edudb\\:\\:(\\w+)\\:(\\w+)\\]\\z";

    @Override
    public void setNextElementInChain(ConsoleExecutorChain chainElement) {
        this.nextElement = chainElement;
    }

    //	TODO: handle this logic
    @Override
    public Response execute(Request request) {
        String command = request.getCommand();
        if (command.toLowerCase().startsWith("[edudb::")) {
            Matcher matcher = Utility.getMatcher(command, regex);
            if (matcher.matches()) {
                /**
                 * Write anything to the client to initialize a connection with
                 * it.
                 *
                 */
                if (matcher.group(1).equals("admin") && matcher.group(2).equals("admin")) {
                    return new Response("[edudb::init]");
                } else {
                    return new Response("[edudb::mismatch]");
                }

            }
        }
        return nextElement.execute(request);
    }

}
