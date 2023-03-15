/*
 *
 * EduDB is made available under the OSI-approved MIT license.
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 * /
 */

package net.edudb;


import net.edudb.authentication.JwtUtil;
import net.edudb.engine.Config;
import net.edudb.executors.*;
import net.edudb.statistics.Schema;

import java.util.concurrent.*;

public class ServerHandler implements RequestHandler {
    private ConsoleExecutorChain chain;

    public ServerHandler() {
        ConsoleExecutorChain[] executorChain = {
                new InitializeExecutor(), //TODO: take care of this
                new CreateUserExecutor(),
                new DropUserExecutor(),
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
//        Response response = chain.execute(request);
//        return response;
        if (!JwtUtil.isValidToken(request.getAuthToken())) {
            return new Response("Invalid token", ResponseStatus.ERROR);
        }

        return handleThread(request);
    }

    public Response handleThread(Request request) {
        String workspace = JwtUtil.getUsername(request.getAuthToken());
        String databaseName = request.getDatabaseName();

        ExecutorService executor = Executors.newSingleThreadExecutor();

        Callable<Response> callable = () -> {
            Config.setCurrentWorkspace(workspace);
            Config.setCurrentDatabaseName(databaseName);
            Schema.getInstance();
            return chain.execute(request);
        };

        Future<Response> future = executor.submit(callable);

        executor.shutdown();
        while (!executor.isTerminated()) {
            // Wait for the thread to complete
        }

        Response response = null;
        try {
            response = future.get();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }

        return response;
    }
}
