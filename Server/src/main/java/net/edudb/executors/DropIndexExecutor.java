/*
 *
 * EduDB is made available under the OSI-approved MIT license.
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 * /
 */

package net.edudb.executors;

import hu.webarticum.regexbee.Bee;
import net.edudb.Request;
import net.edudb.Response;
import net.edudb.ResponseStatus;
import net.edudb.engine.DatabaseEngine;
import net.edudb.exception.IndexNotFoundException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DropIndexExecutor implements ConsoleExecutorChain {
    private ConsoleExecutorChain nextElement;
    private static final Pattern DROP_INDEX_PATTERN = Bee
            .then(Bee.BEGIN)
            .then(Bee.fixed("DROP INDEX").caseInsensitive())
            .then(Bee.WHITESPACE.occurAtLeast(1))
            .then(Bee.fixed("ON").caseInsensitive())
            .then(Bee.WHITESPACE.occurAtLeast(1))
            .then(Bee.checked("[a-zA-Z0-9_]+").as("table"))
            .then(Bee.WHITESPACE.occurAtLeast(0))
            .then(Bee.fixed("("))
            .then(Bee.WHITESPACE.occurAtLeast(0))
            .then(Bee.checked("[a-zA-Z0-9_]+").as("column"))
            .then(Bee.WHITESPACE.occurAtLeast(0))
            .then(Bee.fixed(")"))
            .then(Bee.fixed(";").optional())
            .then(Bee.END)
            .toPattern();

    @Override
    public void setNextElementInChain(ConsoleExecutorChain chainElement) {
        this.nextElement = chainElement;
    }


    @Override
    public Response execute(Request request) {
        String command = request.getCommand();
        Matcher matcher = DROP_INDEX_PATTERN.matcher(command);

        if (!matcher.matches()) {
            return nextElement.execute(request);
        }

        String workspaceName = request.getWorkspaceName();
        String databaseName = request.getDatabaseName();
        String tableName = matcher.group("table");
        String columnName = matcher.group("column");

        try {
            DatabaseEngine.getInstance().dropIndex(workspaceName, databaseName, tableName, columnName);
            return new Response("Index dropped successfully", ResponseStatus.OK);
        } catch (IndexNotFoundException e) {
            return new Response(e.getMessage(), ResponseStatus.ERROR);
        }
    }
}
