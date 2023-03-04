/*
 *
 * EduDB is made available under the OSI-approved MIT license.
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 * /
 */

package net.edudb.newserver;

import net.edudb.newserver.executors.*;
import net.edudb.request.Request;
import net.edudb.response.Response;

public class ServerHandler {
    private ConsoleExecutorChain chain;

    public ServerHandler() {
        ConsoleExecutorChain[] executorChain = {
                new InitializeExecutor(), //TODO: take care of this
                new ListDatabasesExecutor(),
                new CreateDatabaseExecutor(),
                new DropDatabaseExecutor(),
                new OpenDatabaseExecutor(),
                new CloseDatabaseExecutor(),
                new CopyExecutor(),
                new DropTableExecutor(),
                new SQLExecutor(),
        };

        this.chain = connectChain(executorChain);
    }

    private static ConsoleExecutorChain connectChain(ConsoleExecutorChain[] chain) {
        for (int i = 0; i < chain.length - 1; i++) {
            chain[i].setNextElementInChain(chain[i + 1]);
        }
        return chain[0];
    }

    public Response handle(Request request) {
        Response response = chain.execute(request.getCommand());
        return response;
    }
}
