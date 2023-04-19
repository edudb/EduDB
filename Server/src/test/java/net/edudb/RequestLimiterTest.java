/*
 *
 * EduDB is made available under the OSI-approved MIT license.
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 * /
 */

package net.edudb;

import com.github.fppt.jedismock.RedisServer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import redis.clients.jedis.Jedis;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.testcontainers.shaded.org.awaitility.Awaitility.await;

class RequestLimiterTest {
    private RequestLimiter underTest;
    private static final int REQUESTS_NUMBER_LIMIT = 2;
    private static final long DURATION_OF_REQUESTS_LIMIT_IN_SECONDS = 2;
    private static final String USER_ID = "user1";

    @BeforeEach
    void setUp() throws IOException {
        // mock redis server
        RedisServer server = RedisServer
                .newRedisServer()
                .start();
        Jedis jedis = new Jedis(server.getHost(), server.getBindPort());
        underTest = new RequestLimiter(jedis, REQUESTS_NUMBER_LIMIT, DURATION_OF_REQUESTS_LIMIT_IN_SECONDS);
    }

    @Test
    @DisplayName("Should allow request if the number of requests is less than the limit")
    void allowRequest() {
        assertThat(underTest.allowRequest(USER_ID)).isTrue();
    }

    @Test
    @DisplayName("Should allow request if the number of requests is equal to the limit")
    void allowRequestWhenEqual() {
        for (int i = 0; i < REQUESTS_NUMBER_LIMIT - 1; i++) {
            underTest.allowRequest(USER_ID);
        }
        assertThat(underTest.allowRequest(USER_ID)).isTrue();
    }

    @Test
    @DisplayName("Should not allow request if the number of requests is greater than the limit")
    void notAllowRequest() {
        for (int i = 0; i < REQUESTS_NUMBER_LIMIT; i++) {
            underTest.allowRequest(USER_ID);
        }
        assertThat(underTest.allowRequest(USER_ID)).isFalse();
    }

    @Test
    @DisplayName("Should reset the number of requests done when a new day starts")
    void resetRequestCount() {
        for (int i = 0; i < REQUESTS_NUMBER_LIMIT; i++) {
            underTest.allowRequest(USER_ID);
        }

        await().atMost(DURATION_OF_REQUESTS_LIMIT_IN_SECONDS + 1, TimeUnit.SECONDS).
                until(() -> underTest.allowRequest(USER_ID));

        boolean allowed = underTest.allowRequest(USER_ID);

        assertThat(allowed).isTrue();
    }
}
