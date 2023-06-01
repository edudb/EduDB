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
import net.edudb.RequestLimiter;
import net.edudb.Response;
import net.edudb.ResponseStatus;
import net.edudb.engine.Config;
import net.edudb.engine.authentication.JwtUtil;
import net.edudb.engine.authentication.UserRole;
import redis.clients.jedis.Jedis;

public class RequestLimiterChecker implements ConsoleExecutorChain {
    private ConsoleExecutorChain nextElement;

    @Override
    public void setNextElementInChain(ConsoleExecutorChain chainElement) {
        this.nextElement = chainElement;
    }


    @Override
    public Response execute(Request request) {
        // request data
        String token = request.getAuthToken();
        String workspace = JwtUtil.getWorkspaceName(token);
        UserRole role = JwtUtil.getUserRole(token);

        if (role == UserRole.ADMIN) {
            return nextElement.execute(request);
        }

        // connect to redis
        String host = System.getProperty("REDIS_HOST", "localhost");
        int port = Integer.parseInt(System.getProperty("REDIS_PORT", "6379"));
        Jedis jedis = new Jedis(host, port);

        // check user daily limit
        int maxRequestsNumber = Config.MAX_REQUESTS_NUMBER_PER_WORKSPACE;
        long duration = Config.DURATION_OF_REQUESTS_LIMIT_IN_SECONDS;
        RequestLimiter requestLimiter = new RequestLimiter(jedis, maxRequestsNumber, duration);

        if (!requestLimiter.allowRequest(workspace)) {
            return new Response("Number of requests exceeded the daily limit", ResponseStatus.ERROR);
        }
        return nextElement.execute(request);
    }
}
