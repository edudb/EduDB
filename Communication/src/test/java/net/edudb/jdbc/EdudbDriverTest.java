/*
 *
 * EduDB is made available under the OSI-approved MIT license.
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 * /
 */

package net.edudb.jdbc;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.util.Properties;

import static org.junit.Assert.*;

class EdudbDriverTest {
    /**
     * NOTE: This test class requires a running server instance and RabbitMQ.
     */
    private static String SERVER_NAME = "server";
    private static String ADMIN_USERNAME = "admin";
    private static String ADMIN_PASSWORD = "admin";

    @Test
    void testConnection() throws SQLException {
        Connection conn = DriverManager.getConnection(getValidAdminConnectionUrl(), getCredentials());
        assertFalse(conn.isClosed());
        conn.close();
        assertTrue(conn.isClosed());
    }

    @Test
    void testInvalidUrl() {
        Assertions.assertThrows(SQLException.class, () -> {
            DriverManager.getConnection(getInvalidConnectionUrl(), getCredentials());
        });
    }

    @Test
    void testAcceptsUrl() throws SQLException {
        assertTrue(new EdudbDriver().acceptsURL(getValidConnectionUrl()));
        assertFalse(new EdudbDriver().acceptsURL(getInvalidConnectionUrl()));
    }

    @Test
    void testGetPropertyInfo() throws SQLException {
        EdudbDriver driver = new EdudbDriver();
        DriverPropertyInfo[] properties = driver.getPropertyInfo(getValidConnectionUrl(), null);
        assertEquals(0, properties.length);
    }

    private String getValidAdminConnectionUrl() {
        return "jdbc:edudb://" + SERVER_NAME;
    }

    private String getValidConnectionUrl() {
        return "jdbc:edudb://" + SERVER_NAME + ":workspace";
    }

    private String getInvalidConnectionUrl() {
        return "jdbc:edudb:/" + SERVER_NAME + ":workspace";
    }

    private Properties getCredentials() {
        Properties info = new Properties();
        info.setProperty("username", ADMIN_USERNAME);
        info.setProperty("password", ADMIN_PASSWORD);
        return info;
    }
}
