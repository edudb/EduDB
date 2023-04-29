/*
 *
 * EduDB is made available under the OSI-approved MIT license.
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 * /
 */

public class TestUtils {
    public static String createDatabase(String databaseName) {
        return "create database " + databaseName;
    }

    public static String dropDatabase(String databaseName) {
        return "drop database " + databaseName;
    }

    public static String createTable(String tableName, String[] columns, String[] types) {
        String query = "create table " + tableName + " (";
        for (int i = 0; i < columns.length; i++) {
            query += columns[i] + " " + types[i];
        }
        query += ")";
        return query;
    }

    public static String dropTable(String tableName) {
        return "drop table " + tableName;
    }

    public static String insert(String tableName, String[] values) {
        String query = "insert into " + tableName + " values (";
        for (int i = 0; i < values.length; i++) {
            query += values[i] + " ";
        }
        query += ")";
        return query;
    }

    public static String selectAll(String tableName) {
        return "select * from " + tableName;
    }

    public static String select(String tableName, String column, String value) {
        return "select * from " + tableName + " where " + column + " = '" + value + "'";
    }

    public static String update(String tableName, String column, String oldValue, String newValue) {
        String query = "UPDATE " + tableName + " SET " + column + " = '" + newValue + "' where " + column + " = '" + oldValue + "'";
        return query;
    }

    public static String delete(String tableName, String column, String value) {
        String query = "delete from " + tableName + " where " + column + " = '" + value + "'";
        return query;
    }

    public static String createIndex(String tableName, String columnName) {
        return "create index on " + tableName + " (" + columnName + " );";
    }

    public static String dropIndex(String tableName, String columnName) {
        return "drop index on " + tableName + " (" + columnName + " );";
    }

}
