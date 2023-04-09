/*
 *
 * EduDB is made available under the OSI-approved MIT license.
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 * /
 */

package net.edudb.jdbc;

import net.edudb.data_type.TimestampType;
import net.edudb.exception.InvalidTypeValueException;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class EdudbResultSetTest {
    /**
     * NOTE: This test class requires a running server instance and RabbitMQ.
     */
    private static String SERVER_NAME = "server";
    private static String ADMIN_USERNAME = "admin";
    private static String ADMIN_PASSWORD = "admin";
    private static String USER_USERNAME = "test_user";
    private static String USER_PASSWORD = "test_password";
    private static String USER_WORKSPACE;
    private static String DATABASE_NAME = "test_database";
    private static String TABLE_NAME = "test_table_name";
    private static String[][] TABLE_SCHEMA = {
            {"name", "varchar"},
            {"age", "integer"},
            {"male", "boolean"},
            {"salary", "decimal"},
            {"birthday", "timestamp"}
    };
    private static String[][] TABLE_DATA = {
            {"John", "20", "true", "100.0", "2000-01-01 10:00:00"},
            {"Mary", "21", "false", "200.0", "1999-01-01 01:00:00"},
            {"Peter", "22", "true", "300.0", "1990-01-01 00:20:00"}
    };

    private static Connection connection;
    private static Statement statement;
    private static EdudbResultSet resultSet;

    @BeforeAll
    public static void setup() throws ClassNotFoundException, SQLException {
        // create a unique workspace to avoid reaching the limit of requests per day
        USER_WORKSPACE = String.format("TEST_WORKSPACE_%s", System.currentTimeMillis());
        // create the user
        connection = HelperUtils.createConnection(ADMIN_USERNAME, ADMIN_PASSWORD, SERVER_NAME);
        statement = connection.createStatement();
        statement.executeUpdate(CommandsGenerators.createUser(USER_USERNAME, USER_PASSWORD, USER_WORKSPACE));
        connection.close();

        // connect as the user
        connection = HelperUtils.createConnection(USER_USERNAME, USER_PASSWORD, SERVER_NAME, USER_WORKSPACE);
        statement = connection.createStatement();

        // create the database, open it, and create the table
        statement.executeUpdate(CommandsGenerators.createDatabase(DATABASE_NAME));
        statement.executeUpdate(CommandsGenerators.openDatabase(DATABASE_NAME));
        statement.executeUpdate(CommandsGenerators.createTable(TABLE_NAME, TABLE_SCHEMA));

        // insert the data
        for (String[] row : TABLE_DATA) {
            statement.executeUpdate(CommandsGenerators.insertIntoTable(TABLE_NAME, row));
        }
        statement.executeUpdate(CommandsGenerators.closeDatabase());
        connection.close();
    }

    @BeforeEach
    public void beforeEach() throws SQLException, ClassNotFoundException {
        // connect as the user
        connection = HelperUtils.createConnection(USER_USERNAME, USER_PASSWORD, SERVER_NAME, USER_WORKSPACE);
        statement = connection.createStatement();
        statement.executeUpdate(CommandsGenerators.openDatabase(DATABASE_NAME));
        resultSet = (EdudbResultSet) statement.executeQuery(CommandsGenerators.selectFromTable(TABLE_NAME));
    }

    @Test
    void testResultSetAccessStringData() throws SQLException {
        while (resultSet.next()) {
            Assertions.assertEquals(resultSet.getString(0),
                    TABLE_DATA[resultSet.getRow() - 1][0]);
            Assertions.assertEquals(resultSet.getString(TABLE_SCHEMA[0][0]),
                    TABLE_DATA[resultSet.getRow() - 1][0]);
        }
    }

    @Test
    void testResultSetAccessIntData() throws SQLException {
        while (resultSet.next()) {
            Assertions.assertEquals(resultSet.getInt(1),
                    Integer.parseInt(TABLE_DATA[resultSet.getRow() - 1][1]));
            Assertions.assertEquals(resultSet.getInt(TABLE_SCHEMA[1][0]),
                    Integer.parseInt(TABLE_DATA[resultSet.getRow() - 1][1]));
        }
    }

    @Test
    void testResultSetAccessBooleanData() throws SQLException {
        while (resultSet.next()) {
            Assertions.assertEquals(resultSet.getBoolean(2),
                    Boolean.parseBoolean(TABLE_DATA[resultSet.getRow() - 1][2]));
            Assertions.assertEquals(resultSet.getBoolean(TABLE_SCHEMA[2][0]),
                    Boolean.parseBoolean(TABLE_DATA[resultSet.getRow() - 1][2]));
        }
    }

    @Test
    void testResultSetAccessDoubleData() throws SQLException {
        while (resultSet.next()) {
            Assertions.assertEquals(resultSet.getDouble(3),
                    Double.parseDouble(TABLE_DATA[resultSet.getRow() - 1][3]));
            Assertions.assertEquals(resultSet.getDouble(TABLE_SCHEMA[3][0]),
                    Double.parseDouble(TABLE_DATA[resultSet.getRow() - 1][3]));
        }
    }

    @Test
    void testResultSetAccessDateData() throws SQLException, InvalidTypeValueException {
        while (resultSet.next()) {
            Assertions.assertEquals(resultSet.getTimestamp(4).getTime(),
                    TimestampType.parseTimestamp(TABLE_DATA[resultSet.getRow() - 1][4]).getTime());
            Assertions.assertEquals(resultSet.getTimestamp(TABLE_SCHEMA[4][0]).getTime(),
                    TimestampType.parseTimestamp(TABLE_DATA[resultSet.getRow() - 1][4]).getTime());
        }
    }

    @Test
    void testResultSetMovement() throws SQLException {
        Assertions.assertTrue(resultSet.isBeforeFirst());
        resultSet.next();
        Assertions.assertTrue(resultSet.isFirst());
        resultSet.beforeFirst();
        Assertions.assertTrue(resultSet.isBeforeFirst());
        resultSet.first();
        Assertions.assertTrue(resultSet.isFirst());
        Assertions.assertEquals(1, resultSet.getRow());
        // relative
        resultSet.relative(1);
        Assertions.assertEquals(2, resultSet.getRow());
        resultSet.relative(-1);
        Assertions.assertEquals(1, resultSet.getRow());
        // absolute
        resultSet.absolute(2);
        Assertions.assertEquals(2, resultSet.getRow());
        resultSet.absolute(1);
        Assertions.assertEquals(1, resultSet.getRow());

    }


    @AfterEach
    public void afterEach() throws SQLException {
        resultSet.close();
        connection.close();
    }

    @AfterAll
    public static void tearDown() throws ClassNotFoundException, SQLException {
        // connect as the admin and drop the user
        connection = HelperUtils.createConnection(ADMIN_USERNAME, ADMIN_PASSWORD, SERVER_NAME);
        statement = connection.createStatement();
        statement.executeUpdate(CommandsGenerators.dropWorkspace(USER_WORKSPACE));
        connection.close();
    }
}
