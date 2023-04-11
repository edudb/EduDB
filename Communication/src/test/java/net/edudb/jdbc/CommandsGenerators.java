/*
 *
 * EduDB is made available under the OSI-approved MIT license.
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 * /
 */

package net.edudb.jdbc;/*
 *
 * EduDB is made available under the OSI-approved MIT license.
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 * /
 */

public class CommandsGenerators {

    static String dropWorkspace(String workspace) {
        return String.format("DROP WORKSPACE %s", workspace);
    }

    static String createUser(String username, String password, String workspace, String role) {
        return String.format("CREATE USER %s WITH PASSWORD=\"%s\" AS %s IN WORKSPACE=\"%s\"", username, password, role, workspace);
    }

    static String createUser(String username, String password, String workspace) {
        return String.format("CREATE USER %s WITH PASSWORD=\"%s\" IN WORKSPACE=\"%s\"", username, password, workspace);
    }

    static String dropUser(String username, String workspace) {
        return String.format("DROP USER %s from workspace=\"%s\"", username, workspace);
    }

    static String createDatabase(String databaseName) {
        return String.format("CREATE DATABASE %s", databaseName);
    }

    static String openDatabase(String databaseName) {
        return String.format("OPEN DATABASE %s", databaseName);
    }

    static String closeDatabase() {
        return "CLOSE DATABASE";
    }

    static String dropDatabase(String databaseName) {
        return String.format("DROP DATABASE %s", databaseName);
    }

    static String createTable(String tableName, String[][] tableSchema) {
        String tableSchemaString = "";
        for (String[] column : tableSchema) {
            tableSchemaString += String.format(" %s %s,", column[0], column[1]);
        }
        tableSchemaString = tableSchemaString.substring(0, tableSchemaString.length() - 1);
        return String.format("CREATE TABLE %s (%s)", tableName, tableSchemaString);
    }

    static String dropTable(String tableName) {
        return String.format("DROP TABLE %s", tableName);
    }

    static String insertIntoTable(String tableName, String[] values) {
        String valuesString = "";
        for (String value : values) {
            valuesString += String.format("'%s',", value);
        }
        valuesString = valuesString.substring(0, valuesString.length() - 1);
        return String.format("INSERT INTO %s VALUES (%s)", tableName, valuesString);
    }

    static String selectFromTable(String tableName) {
        return String.format("SELECT * FROM %s", tableName);
    }

}
