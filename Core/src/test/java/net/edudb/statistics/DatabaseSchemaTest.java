/*
 *
 * EduDB is made available under the OSI-approved MIT license.
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 * /
 */

package net.edudb.statistics;

import net.edudb.TestUtils;
import net.edudb.engine.Config;
import net.edudb.exception.TableNotFoundException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;

public class DatabaseSchemaTest {
    private static final String WORKSPACE_NAME = "workspace";
    private static final String DATABASE_NAME = "database";
    private static final String[] TABLES = {"table1 name varchar", "table2 state varchar"};

    @BeforeAll
    public static void setup() throws FileNotFoundException {
        for (String table : TABLES) {
            TestUtils.createDirectory(Config.tablesPath(WORKSPACE_NAME, DATABASE_NAME));
            TestUtils.createFile(Config.tablePath(WORKSPACE_NAME, DATABASE_NAME, table.split(" ")[0]));
            TestUtils.appendLineToFile(Config.schemaPath(WORKSPACE_NAME, DATABASE_NAME), table);
        }
    }

    @AfterAll
    public static void tearDown() {
        TestUtils.deleteDirectory(Config.absolutePath());
    }

    @Test
    public void test() throws TableNotFoundException {
        DatabaseSchema databaseSchema = new DatabaseSchema(WORKSPACE_NAME, DATABASE_NAME);
        Assertions.assertEquals(DATABASE_NAME, databaseSchema.getDatabaseName());
        for (String table : TABLES) {
            String tableName = table.split(" ")[0];
            Assertions.assertNotNull(databaseSchema.getTable(tableName));
            Assertions.assertEquals(tableName, databaseSchema.getTable(tableName).getTableName());
        }
    }

}
