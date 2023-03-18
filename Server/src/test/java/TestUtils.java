/*
 *
 * EduDB is made available under the OSI-approved MIT license.
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 * /
 */

import java.io.File;
import java.util.function.Function;

public class TestUtils {
    public static Function<String, String> createDatabase = (databaseName) -> "create database " + databaseName;
    public static Function<String, String> openDatabase = (databaseName) -> "open database " + databaseName;
    public static Function<String, String> dropDatabase = (databaseName) -> "drop database " + databaseName;
    public static Function<String, String> createTable = (tableName) -> "create table " + tableName + " (name varchar)";
    public static Function<String, String> dropTable = (tableName) -> "drop table " + tableName;
    public static Function<String, String> insert = (tableName) -> "insert into " + tableName + " values ('test')";
    public static Function<String, String> select = (tableName) -> "select * from " + tableName;

    public static boolean deleteDirectory(File directoryToBeDeleted) {
        File[] allContents = directoryToBeDeleted.listFiles();
        if (allContents != null) {
            for (File file : allContents) {
                deleteDirectory(file);
            }
        }
        return directoryToBeDeleted.delete();
    }
}
