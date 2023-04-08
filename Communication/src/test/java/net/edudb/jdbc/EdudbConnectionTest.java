/*
 *
 * EduDB is made available under the OSI-approved MIT license.
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 * /
 */

package net.edudb.jdbc;

import net.edudb.Response;
import net.edudb.ResponseStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.sql.Statement;

public class EdudbConnectionTest {
    private static String SERVER_NAME = "server";
    private static String ADMIN_USERNAME = "admin";
    private static String ADMIN_PASSWORD = "admin";
    private static String BAD_USERNAME = "bad_username";
    private static String BAD_PASSWORD = "bad_password";
    private static String USER_USERNAME = "test_user";
    private static String USER_PASSWORD = "test_password";
    private static String USER_WORKSPACE_NAME = "workspace";

    @Test
    public void testConstructorWithGoodCredentials() throws SQLException {
        EdudbConnection conn = new EdudbConnection(SERVER_NAME, null, ADMIN_USERNAME, ADMIN_PASSWORD);
        Assertions.assertNotNull(conn);
        Assertions.assertNotNull(conn.getAuthToken());
    }

    @Test
    public void testConstructorThrowsExceptionOnBadCredentials() {
        Assertions.assertThrows(SQLException.class, () -> {
            new EdudbConnection(SERVER_NAME, null, BAD_USERNAME, BAD_PASSWORD);
        });
    }

    @Test
    public void testSendSqlCommandReturnsResponse() throws SQLException {
        EdudbConnection conn = new EdudbConnection(SERVER_NAME, null, ADMIN_USERNAME, ADMIN_PASSWORD);
        Response response = conn.sendSqlCommand(CommandsGenerators.createUser(USER_USERNAME, USER_PASSWORD, USER_WORKSPACE_NAME));
        Assertions.assertNotNull(response);
        Assertions.assertEquals(ResponseStatus.OK, response.getStatus());
        response = conn.sendSqlCommand(CommandsGenerators.dropUser(USER_USERNAME, USER_WORKSPACE_NAME));
        Assertions.assertNotNull(response);
        Assertions.assertEquals(ResponseStatus.OK, response.getStatus());
    }


    @Test
    public void testGetClientReturnsClient() throws SQLException {
        EdudbConnection conn = new EdudbConnection(SERVER_NAME, null, ADMIN_USERNAME, ADMIN_PASSWORD);
        Assertions.assertNotNull(conn.getClient());
    }

    @Test
    public void testCreateStatementReturnsStatement() throws SQLException {
        EdudbConnection conn = new EdudbConnection(SERVER_NAME, null, ADMIN_USERNAME, ADMIN_PASSWORD);
        Statement statement = conn.createStatement();
        Assertions.assertNotNull(statement);
    }

}