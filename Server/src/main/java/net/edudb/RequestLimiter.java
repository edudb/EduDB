/*
 *
 * EduDB is made available under the OSI-approved MIT license.
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 * /
 */

package net.edudb;

import redis.clients.jedis.Jedis;

import java.time.LocalDate;
import java.time.ZoneOffset;

public class RequestLimiter {
    private static RequestLimiter instance;
    private final Jedis jedis;
    private final int maxRequestsPerDay = 1000;

    private RequestLimiter() {
        String host = System.getProperty("REDIS_HOST", "localhost");
        int port = Integer.parseInt(System.getProperty("REDIS_PORT", "6379"));
        this.jedis = new Jedis(host, port);
    }

    public static RequestLimiter getInstance() {
        if (instance == null) {
            synchronized (RequestLimiter.class) {
                if (instance == null) {
                    instance = new RequestLimiter();
                }
            }
        }
        return instance;
    }

    public boolean allowRequest(String userId) {
        LocalDate now = LocalDate.now();
        long epochStart = now.atStartOfDay().toEpochSecond(ZoneOffset.UTC);
        String key = String.format("%s:%d", userId, epochStart);
        long requestCount = jedis.incr(key);
        if (requestCount == 1) {
            jedis.expire(key, 24 * 60 * 60); // expire in 24 hours
        }
        return requestCount <= maxRequestsPerDay;
    }
}
