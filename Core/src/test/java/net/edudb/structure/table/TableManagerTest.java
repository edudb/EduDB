/*
 *
 * EduDB is made available under the OSI-approved MIT license.
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 * /
 */

package net.edudb.structure.table;

import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;
import net.edudb.engine.Config;
import net.edudb.exception.DatabaseNotFoundException;
import net.edudb.exception.TableNotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TableManagerTest {
    private static FileSystem fs;
    @Spy
    private TableManager tableManagerSpy;
    private static final String WORKSPACE_NAME = "workspace";
    private static final String DATABASE_NAME = "database";


    @BeforeEach
    void setUp() throws IOException {
        fs = Jimfs.newFileSystem(Configuration.unix());
        Config.setAbsolutePath(fs.getPath("test"));

        Files.createDirectories(Config.tablesPath(WORKSPACE_NAME, DATABASE_NAME));
        Files.createFile(Config.schemaPath(WORKSPACE_NAME, DATABASE_NAME));

    }

    @AfterEach
    void tearDown() throws IOException {
        Config.setAbsolutePath(null);
        fs.close();
    }

    @Test
    @DisplayName("should delete a table file and update the schema file")
    void testDeleteTable1() throws IOException, TableNotFoundException, DatabaseNotFoundException {
        String tableName = "table";
        String schema = tableName + " id integer, name varchar";

        Files.createFile(Config.tablePath(WORKSPACE_NAME, DATABASE_NAME, tableName));
        Files.write(Config.schemaPath(WORKSPACE_NAME, DATABASE_NAME), schema.getBytes());

        Table mockTable = mock(Table.class);
        doNothing().when(mockTable).deletePages();
        doReturn(mockTable).when(tableManagerSpy).readTable(anyString(), anyString(), anyString());

        tableManagerSpy.deleteTable(WORKSPACE_NAME, DATABASE_NAME, tableName);

        assertThat(Config.tablePath(WORKSPACE_NAME, DATABASE_NAME, tableName)).doesNotExist();
        assertThat(Config.schemaPath(WORKSPACE_NAME, DATABASE_NAME)).exists().hasContent("");
    }

    @Test
    @DisplayName("should delete the correct table when two tables have the same prefix")
    void testDeleteTable2() throws IOException, TableNotFoundException, DatabaseNotFoundException {
        String tableName1 = "tableNotToBeDeleted";
        String schema1 = tableName1 + " id integer,name varchar";
        String tableName2 = "table";
        String schema2 = tableName2 + " id integer,name varchar";

        Files.createFile(Config.tablePath(WORKSPACE_NAME, DATABASE_NAME, tableName1));
        Files.createFile(Config.tablePath(WORKSPACE_NAME, DATABASE_NAME, tableName2));
        Files.write(Config.schemaPath(WORKSPACE_NAME, DATABASE_NAME), schema1.getBytes());
        Files.write(Config.schemaPath(WORKSPACE_NAME, DATABASE_NAME), ("\n" + schema2).getBytes(), StandardOpenOption.APPEND);

        Table mockTable = mock(Table.class);
        doNothing().when(mockTable).deletePages();
        doReturn(mockTable).when(tableManagerSpy).readTable(anyString(), anyString(), anyString());

        tableManagerSpy.deleteTable(WORKSPACE_NAME, DATABASE_NAME, tableName2);

        assertThat(Config.tablePath(WORKSPACE_NAME, DATABASE_NAME, tableName2)).doesNotExist();
        assertThat(Config.tablePath(WORKSPACE_NAME, DATABASE_NAME, tableName1)).exists();
        assertThat(Files.readAllLines(Config.schemaPath(WORKSPACE_NAME, DATABASE_NAME))).hasSize(1);
        assertThat(Config.schemaPath(WORKSPACE_NAME, DATABASE_NAME)).exists().hasContent(schema1);
    }
}
