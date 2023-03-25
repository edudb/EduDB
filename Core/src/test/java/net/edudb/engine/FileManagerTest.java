/*
 *
 * EduDB is made available under the OSI-approved MIT license.
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 * /
 */

package net.edudb.engine;

import net.edudb.TestUtils;
import net.edudb.exception.DatabaseAlreadyExistException;
import net.edudb.exception.DirectoryAlreadyExistsException;
import net.edudb.exception.DirectoryNotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.List;

public class FileManagerTest {
    private static FileManager fileManager = FileManager.getInstance();
    private static final String[] DIRECTORIES = {"dir1", "dir2"};
    private static final String NON_EXISTING_DIRECTORY = "nonExistingDirectory";
    private static final String[] FILES = {"file1", "file2"};
    private static final String NON_EXISTING_FILE = "nonExistingFile";
    private static final String[] WORKSPACES = {"workspace1", "workspace2"};
    private static final String[] DATABASES = {"database1", "database2"};
    private static final String[] TABLES = {"table1", "table2"};

    @BeforeEach
    public void setup() throws DirectoryAlreadyExistsException, DirectoryNotFoundException {
        TestUtils.createDirectory(Config.absolutePath());
    }

    @AfterEach
    public void tearDown() {
        TestUtils.deleteDirectory(Config.absolutePath());
    }

    @Test
    public void testListDirectories() throws DirectoryNotFoundException, DirectoryAlreadyExistsException {
        for (String dir : DIRECTORIES) {
            fileManager.createDirectory(Config.absolutePath() + File.separator + dir);
        }
        String[] listedDirectories = fileManager.listDirectories(Config.absolutePath());
        Arrays.sort(listedDirectories);
        Assertions.assertArrayEquals(DIRECTORIES, listedDirectories);
    }

    @Test
    public void testListDirectoriesOfNonExistingDirectory() {
        Assertions.assertThrows(DirectoryNotFoundException.class, () -> {
            fileManager.listDirectories(Config.absolutePath() + File.separator + NON_EXISTING_DIRECTORY);
        });
    }

    @Test
    public void testListFiles() throws DirectoryNotFoundException {
        for (String file : FILES) {
            TestUtils.createFile(Config.absolutePath() + File.separator + file);
        }
        String[] listedFiles = fileManager.listFiles(Config.absolutePath());
        Arrays.sort(listedFiles);
        Assertions.assertArrayEquals(FILES, listedFiles);
    }

    @Test
    public void testListFilesOfNonExistingDirectory() {
        Assertions.assertThrows(DirectoryNotFoundException.class, () -> {
            fileManager.listFiles(Config.absolutePath() + File.separator + NON_EXISTING_DIRECTORY);
        });
    }

    @Test
    public void testListWorkspaces() throws DirectoryNotFoundException, DirectoryAlreadyExistsException {
        for (String workspace : WORKSPACES) {
            fileManager.createDirectory(Config.workspacePath(workspace));
        }
        String[] listedWorkspaces = fileManager.listWorkspaces();
        Arrays.sort(listedWorkspaces);
        Assertions.assertArrayEquals(WORKSPACES, listedWorkspaces);
    }

    @Test
    public void testListDatabases() throws DirectoryNotFoundException, DirectoryAlreadyExistsException {
        for (String database : DATABASES) {
            fileManager.createDirectory(Config.databasePath(WORKSPACES[0], database));
        }
        String[] listedDatabases = fileManager.listDatabases(WORKSPACES[0]);
        Arrays.sort(listedDatabases);
        Assertions.assertArrayEquals(DATABASES, listedDatabases);
    }

    @Test
    public void testListTables() throws DirectoryNotFoundException, DatabaseAlreadyExistException {
        fileManager.createDatabase(WORKSPACES[0], DATABASES[0]);
        for (String table : TABLES) {
            TestUtils.createFile(Config.tablePath(WORKSPACES[0], DATABASES[0], table));
        }
        String[] listedTables = fileManager.listTables(WORKSPACES[0], DATABASES[0]);
        Arrays.sort(listedTables);
        Assertions.assertArrayEquals(TABLES, listedTables);
    }

    @Test
    public void testReadSchemaFile() throws FileNotFoundException, DatabaseAlreadyExistException {
        fileManager.createDatabase(WORKSPACES[0], DATABASES[0]);
        TestUtils.createFile(Config.schemaPath(WORKSPACES[0], DATABASES[0]));
        fileManager.appendLineToFile(Config.schemaPath(WORKSPACES[0], DATABASES[0]), "schema");
        List<String> lines = fileManager.readSchemaFile(WORKSPACES[0], DATABASES[0]);
        Assertions.assertEquals(1, lines.size());
        Assertions.assertEquals("schema", lines.get(0));
    }

    @Test
    public void testReadSchemaFileOfNonExistingDatabase() throws DatabaseAlreadyExistException {
        TestUtils.createDirectory(Config.databasePath(WORKSPACES[0], DATABASES[0]));
        Assertions.assertThrows(FileNotFoundException.class, () -> {
            fileManager.readSchemaFile(WORKSPACES[0], DATABASES[0]);
        });
    }

}
