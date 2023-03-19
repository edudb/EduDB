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
import net.edudb.parser.Parser;

/**
 * Handles the parsing and execution of an SQL query.
 *
 * @author Ahmed Abdul Badie
 */
public class SQLExecutor implements ConsoleExecutorChain {

    @Override
    public void setNextElementInChain(ConsoleExecutorChain chainElement) {
        throw new UnsupportedOperationException("SQLExecutor should be the last element in the chain");
    }

    @Override
    public Response execute(Request request) {

        String databaseName = request.getDatabaseName();

        if (databaseName == null) {
            return new Response("You must open a database first", ResponseStatus.ERROR);
        }

        Parser parser = new Parser();
        return parser.parseSQL(request.getCommand());
    }
}
