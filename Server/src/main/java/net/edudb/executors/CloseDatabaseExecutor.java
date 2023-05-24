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
import net.edudb.ResponseStatus;
import net.edudb.engine.DatabaseEngine;
import net.edudb.engine.Utility;
import net.edudb.exception.DatabaseNotFoundException;
import net.edudb.exception.WorkspaceNotFoundException;

import java.util.regex.Matcher;

/**
 * Closes the current open database.
 *
 * @author Ahmed Abdul Badie
 */
public class CloseDatabaseExecutor implements ConsoleExecutorChain {
    private ConsoleExecutorChain nextElement;
    /**
     * Matches strings of the form: <br>
     * <br>
     * <b>CLOSE DATABASE;<b><br>
     * <br>
     */
    private static final String REGEX = "\\A(?:(?i)close)\\s+(?:(?i)database)\\s*;?\\z";

    @Override
    public void setNextElementInChain(ConsoleExecutorChain chainElement) {
        this.nextElement = chainElement;
    }

    @Override
    public Response execute(Request request) {
        String command = request.getCommand();
        Matcher matcher = Utility.getMatcher(command, REGEX);

        if (!matcher.matches()) {
            return nextElement.execute(request);
        }
        String workspaceName = request.getWorkspaceName();
        String databaseName = request.getDatabaseName();

        if (databaseName == null) {
            return new Response("No database is open.", ResponseStatus.ERROR);
        }

        try {
            DatabaseEngine.getInstance().closeDatabase(workspaceName, databaseName);
            Response response = new Response("Database " + databaseName + " closed successfully.", true, null);
            response.setStatus(ResponseStatus.OK);
            return response;
        } catch (DatabaseNotFoundException | WorkspaceNotFoundException e) {
            e.printStackTrace();
            return new Response(e.getMessage(), ResponseStatus.ERROR);
        }
    }

}
