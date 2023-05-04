/*
 *
 * EduDB is made available under the OSI-approved MIT license.
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 * /
 */

public class CommandsGenerators {

    static String dropWorkspace(String workspace) {
        String query = String.format("DROP WORKSPACE %s", workspace);
        System.out.println(query);
        return query;
    }

    static String createUser(String username, String password, String workspace, String role) {
        String query = String.format("CREATE USER %s WITH PASSWORD=\"%s\" AS %s IN WORKSPACE=\"%s\"", username, password, role, workspace);
        System.out.println(query);
        return query;
    }

    static String createUser(String username, String password, String workspace) {
        String query = String.format("CREATE USER %s WITH PASSWORD=\"%s\" IN WORKSPACE=\"%s\"", username, password, workspace);
        System.out.println(query);
        return query;
    }

    static String dropUser(String username, String workspace) {
        String query = String.format("DROP USER %s from workspace=\"%s\"", username, workspace);
        System.out.println(query);
        return query;
    }

    static String createDatabase(String databaseName) {
        String query = String.format("CREATE DATABASE %s", databaseName);
        System.out.println(query);
        return query;
    }

    static String openDatabase(String databaseName) {
        String query = String.format("OPEN DATABASE %s", databaseName);
        System.out.println(query);
        return query;
    }

    static String closeDatabase() {
        return "CLOSE DATABASE";
    }

    static String dropDatabase(String databaseName) {
        String query = String.format("DROP DATABASE %s", databaseName);
        System.out.println(query);
        return query;
    }

    static String createTable(String tableName, String[][] tableSchema) {
        String tableSchemaString = "";
        for (String[] column : tableSchema) {
            tableSchemaString += String.format("%s %s, ", column[0], column[1]);
        }
        tableSchemaString = tableSchemaString.substring(0, tableSchemaString.length() - 2);
        String query = String.format("CREATE TABLE %s (%s)", tableName, tableSchemaString);
        System.out.println(query);
        return query;
    }

    static String dropTable(String tableName) {
        String query = String.format("DROP TABLE %s", tableName);
        System.out.println(query);
        return query;
    }

    static String insertIntoTable(String tableName, String[] values) {
        String valuesString = "";
        for (String value : values) {
            valuesString += String.format("'%s', ", value);
        }
        valuesString = valuesString.substring(0, valuesString.length() - 2);
        String query = String.format("INSERT INTO %s VALUES (%s)", tableName, valuesString);
        System.out.println(query);
        return query;
    }

    static String selectFromTable(String tableName) {
        String query = String.format("SELECT * FROM %s", tableName);
        System.out.println(query);
        return query;
    }

    static String selectFromTable(String tableName, String[][] conditions) {
        String conditionsString = "";
        for (String[] condition : conditions) {
            conditionsString += String.format("%s='%s' AND ", condition[0], condition[1]);
        }
        conditionsString = conditionsString.substring(0, conditionsString.length() - 5);
        String query = String.format("SELECT * FROM %s WHERE %s", tableName, conditionsString);
        System.out.println(query);
        return query;
    }

    static String updateTable(String tableName, String[][] values, String[][] conditions) {
        String valuesString = "";
        for (String[] value : values) {
            valuesString += String.format("%s='%s' ,", value[0], value[1]);
        }
        valuesString = valuesString.substring(0, valuesString.length() - 1);
        String conditionsString = "";
        for (String[] condition : conditions) {
            conditionsString += String.format("%s='%s' AND ", condition[0], condition[1]);
        }
        conditionsString = conditionsString.substring(0, conditionsString.length() - 5);
        String query = String.format("UPDATE %s SET %s WHERE %s", tableName, valuesString, conditionsString);
        System.out.println(query);
        return query;
    }

    static String deleteFromTable(String tableName, String[][] conditions) {
        String conditionsString = "";
        for (String[] condition : conditions) {
            conditionsString += String.format("%s='%s' AND ", condition[0], condition[1]);
        }
        conditionsString = conditionsString.substring(0, conditionsString.length() - 5);
        String query = String.format("DELETE FROM %s WHERE %s", tableName, conditionsString);
        System.out.println(query);
        return query;
    }

    static String createIndex(String tableName, String columnName) {
        String query = String.format("CREATE INDEX ON %s (%s)", tableName, columnName);
        System.out.println(query);
        return query;
    }

    static String dropIndex(String tableName, String columnName) {
        String query = String.format("DROP INDEX ON %s (%s)", tableName, columnName);
        System.out.println(query);
        return query;
    }
}
