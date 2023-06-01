/*
 *
 * EduDB is made available under the OSI-approved MIT license.
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 * /
 */

package net.edudb.index;

import net.edudb.engine.Config;
import net.edudb.exception.DatabaseNotFoundException;
import net.edudb.exception.IndexAlreadyExistException;
import net.edudb.exception.IndexNotFoundException;
import net.edudb.exception.TableAlreadyExistException;
import net.edudb.page.PageManager;
import net.edudb.structure.table.Table;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IndexManagerTest {
    @Spy
    private IndexManager indexManager;
    @TempDir
    private File tempDir;
    private final String WORKSPACE_NAME = "workspace";
    private final String DATABASE_NAME = "databaseName";
    private final String TABLE_NAME = "tableName";
    private final String COLUMN_NAME = "name";

    @BeforeEach
    void setUp() {
        Config.setAbsolutePath(tempDir.toPath());
    }

    @Test
    void createIndex() throws IndexAlreadyExistException, IOException, TableAlreadyExistException, DatabaseNotFoundException {
        Table table = mock(Table.class);
        when(table.getName()).thenReturn(TABLE_NAME);
        when(table.getPageManager()).thenReturn(new PageManager());
        Files.createDirectories(Config.indexesPath(WORKSPACE_NAME, DATABASE_NAME));

        indexManager.createIndex(WORKSPACE_NAME, DATABASE_NAME, table, COLUMN_NAME);

        assertThat(Config.indexPath(WORKSPACE_NAME, DATABASE_NAME, TABLE_NAME, COLUMN_NAME)).exists();
    }

    @Test
    void dropIndex() throws IOException, IndexNotFoundException {
        Files.createDirectories(Config.indexesPath(WORKSPACE_NAME, DATABASE_NAME));
        Path indexPath = Config.indexPath(WORKSPACE_NAME, DATABASE_NAME, TABLE_NAME, COLUMN_NAME);
        Files.createFile(indexPath);
        doNothing().when(indexManager).removeIndexFromMemory(WORKSPACE_NAME, DATABASE_NAME, TABLE_NAME, COLUMN_NAME);

        indexManager.dropIndex(WORKSPACE_NAME, DATABASE_NAME, TABLE_NAME, COLUMN_NAME);

        assertThat(indexPath).doesNotExist();
    }

    @Test
    void loadDatabaseIndices() throws IOException, DatabaseNotFoundException {
        Files.createDirectories(Config.indexesPath(WORKSPACE_NAME, DATABASE_NAME));
        File file = Config.indexPath(WORKSPACE_NAME, DATABASE_NAME, TABLE_NAME, COLUMN_NAME).toFile();
        new BtreeIndex<>(file);
        indexManager.loadDatabaseIndices(WORKSPACE_NAME, DATABASE_NAME);
        assertThat(indexManager.getIndex(WORKSPACE_NAME, DATABASE_NAME, TABLE_NAME, COLUMN_NAME)).isNotNull();
    }
}
