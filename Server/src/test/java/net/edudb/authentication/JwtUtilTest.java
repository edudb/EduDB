/*
 *
 * EduDB is made available under the OSI-approved MIT license.
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 * /
 */

package net.edudb.authentication;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class JwtUtilTest {
    private static final String USER_NAME = "test_username";
    private static final UserRole ROLE = UserRole.USER;

    @Test
    void generateToken() {
        String token = JwtUtil.generateToken(USER_NAME, ROLE);
        Assertions.assertNotNull(token);
    }

    @Test
    void isValidToken() {
        String token = JwtUtil.generateToken(USER_NAME, ROLE);
        Assertions.assertTrue(JwtUtil.isValidToken(token));
    }

    @Test
    void getUsername() {
        String token = JwtUtil.generateToken(USER_NAME, ROLE);
        Assertions.assertEquals(USER_NAME, JwtUtil.getUsername(token));
    }

    @Test
    void getUserRole() {
        String token = JwtUtil.generateToken(USER_NAME, ROLE);
        Assertions.assertEquals(ROLE, JwtUtil.getUserRole(token));
    }
}
