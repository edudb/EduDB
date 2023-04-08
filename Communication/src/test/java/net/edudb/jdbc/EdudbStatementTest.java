/*
 *
 * EduDB is made available under the OSI-approved MIT license.
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 * /
 */

package net.edudb.jdbc;


import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.Assert.*;

public class EdudbStatementTest {
    /**
     * NOTE: This test class requires a running server instance and RabbitMQ.
     */
    private static String SERVER_NAME = "server";
    private static String ADMIN_USERNAME = "admin";
    private static String ADMIN_PASSWORD = "admin";
    private static Connection connection;
    private static Statement statement;

    @BeforeAll
    public static void setUp() throws SQLException, ClassNotFoundException {
        // create the user
        connection = HelperUtils.createConnection(ADMIN_USERNAME, ADMIN_PASSWORD, SERVER_NAME);
        statement = connection.createStatement();

    }

    @AfterAll
    public static void tearDown() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    @Test
    public void testClose() throws SQLException {
        statement.close();
        assertTrue(statement.isClosed());

        try {
            statement.executeQuery("SELECT * FROM test_table");
            fail("Expected SQLException");
        } catch (SQLException e) {
            // Expected exception
        }
    }

    @Test
    public void testSetFetchSize() throws SQLException {
        int fetchSize = 5;
        statement.setFetchSize(fetchSize);
        assertEquals(fetchSize, statement.getFetchSize());
    }

}