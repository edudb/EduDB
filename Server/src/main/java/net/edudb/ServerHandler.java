/*
 *
 * EduDB is made available under the OSI-approved MIT license.
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 * /
 */

package net.edudb;


import net.edudb.engine.Config;
import net.edudb.engine.authentication.JwtUtil;
import net.edudb.executors.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;

public class ServerHandler implements RequestHandler {
    private static final int MAX_THREADS_PER_USER = 1;
    private Map<String, ExecutorService> usersThreadPools;
    private ConsoleExecutorChain chain;

    public ServerHandler() {
        this.usersThreadPools = new HashMap<>();
        setupOnCloseHandler();

        ConsoleExecutorChain[] executorChain = {
                new AuthenticationChecker(),
                new RequestLimiterChecker(),
                new CreateAdminExecutor(),
                new CreateUserExecutor(),
                new DropAdminExecutor(),
                new DropUserExecutor(),
                new CreateWorkspaceExecutor(),
                new DropWorkspaceExecutor(),
                new NoAdminAccess(), // As admins have no workspace, they can't access any database
                new ListDatabasesExecutor(),
                new CreateDatabaseExecutor(),
                new DropDatabaseExecutor(),
                new OpenDatabaseExecutor(),
                new CloseDatabaseExecutor(),
                new DropTableExecutor(),
                new CreateIndexExecutor(),
                new DropIndexExecutor(),
                new NextRecordsExecutor(),
                new CloseResultSetExecutor(),
                new CopyExecutor(),
                new SQLExecutor(),
        };

        this.chain = connectChain(executorChain);
    }

    private void setupOnCloseHandler() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            for (ExecutorService executorService : usersThreadPools.values()) {
                executorService.shutdown();
            }
        }));
    }

    private static ConsoleExecutorChain connectChain(ConsoleExecutorChain[] chain) {
        for (int i = 0; i < chain.length - 1; i++) {
            chain[i].setNextElementInChain(chain[i + 1]);
        }
        return chain[0];
    }

    public Response handle(Request request) {
        try {
            return handleThread(request);
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    public Response handleThread(Request request) throws InterruptedException, ExecutionException {
        String workspaceName = JwtUtil.getWorkspaceName(request.getAuthToken());
        String databaseName = request.getDatabaseName();

        request.setWorkspaceName(workspaceName);

        usersThreadPools.putIfAbsent(workspaceName, Executors.newFixedThreadPool(MAX_THREADS_PER_USER));


        Callable<Response> callable = () -> {
            Config.setCurrentWorkspace(workspaceName);
            if (databaseName != null) {
                Config.setCurrentDatabaseName(databaseName);
            }
            Response response = chain.execute(request);
            Config.cleanThreadLocal();
            return response;
        };

        Future<Response> future = usersThreadPools.get(workspaceName).submit(callable);

        while (!future.isDone() && !future.isCancelled()) {
            // Wait for the thread to complete
            Thread.sleep(200);
        }

        return future.get();
    }
}
