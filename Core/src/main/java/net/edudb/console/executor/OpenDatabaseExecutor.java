/*
 *
 * EduDB is made available under the OSI-approved MIT license.
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 * /
 */

package net.edudb.console.executor;

import net.edudb.Response;
import net.edudb.engine.DatabaseSystem;
import net.edudb.engine.Utility;

import java.util.regex.Matcher;

/**
 * Opens an available database.
 *
 * @author Ahmed Abdul Badie
 */
public class OpenDatabaseExecutor implements ConsoleExecutorChain {
    /**
     * Matches strings of the form: <br>
     * <br>
     * <b>OPEN DATABASE database_name;</b><br>
     * <br>
     * and captures <b>database_name</b> in the matcher's group one.
     */
    private final String regex = "\\A(?:(?i)open)\\s+(?:(?i)database)\\s+(\\D\\w*)\\s*;?\\z";
    private ConsoleExecutorChain nextElement;

    @Override
    public void setNextElementInChain(ConsoleExecutorChain chainElement) {
        this.nextElement = chainElement;
    }

    @Override
    public Response execute(String string) {
        if (string.toLowerCase().startsWith("open")) {
            Matcher matcher = Utility.getMatcher(string, regex);
            if (matcher.matches()) {
                String databaseName = matcher.group(1);
                boolean databaseOpened = DatabaseSystem.getInstance().open(databaseName);
                if (databaseOpened) {
                    return new Response("Database " + databaseName + " opened successfully.");
                } else {
                    return new Response("Database " + databaseName + " does not exist.");
                }
            }
        }
        return nextElement.execute(string);
    }

}
