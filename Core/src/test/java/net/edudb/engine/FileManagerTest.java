/*
 *
 * EduDB is made available under the OSI-approved MIT license.
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 * /
 */

package net.edudb.engine;

import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;
import net.edudb.exception.DatabaseAlreadyExistException;
import net.edudb.exception.DirectoryAlreadyExistsException;
import net.edudb.exception.DirectoryNotFoundException;
import org.junit.jupiter.api.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FileManagerTest {
    private static FileSystem fs; // in-memory file system for testing
    private static final FileManager fileManager = FileManager.getInstance();
    private static final String[] DIRECTORIES = {"dir1", "dir2"};
    private static final String NON_EXISTING_DIRECTORY = "nonExistingDirectory";
    private static final String[] FILES = {"file1", "file2"};
    private static final String[] WORKSPACES = {"workspace1", "workspace2"};
    private static final String[] DATABASES = {"database1", "database2"};
    private static final String[] TABLES = {"table1", "table2"};

    @BeforeEach
    public void setup() throws DirectoryAlreadyExistsException, DirectoryNotFoundException, IOException {
        fs = Jimfs.newFileSystem(Configuration.unix());
        Config.setAbsolutePath(fs.getPath("test"));
        Files.createDirectories(Config.absolutePath());
    }

    @AfterEach
    public void tearDown() throws IOException {
        Config.setAbsolutePath(null);
        fs.close();
    }

    @Test
    @DisplayName("should return list of lines in file")
    void testReadFile() throws IOException {
        String[] lines = {"line1", "line2"};
        Path path = fs.getPath("test/file");
        Files.write(path, Arrays.asList(lines));

        List<String> readLines = fileManager.readFile(path);

        assertThat(readLines).containsExactly(lines);
    }

    @Test
    @DisplayName("should throw FileNotFoundException if file does not exist")
    void testReadFileOfNonExistingFile() {
        Path path = fs.getPath("test/nonExistingFile");

        assertThatThrownBy(() -> fileManager.readFile(path))
                .isInstanceOf(FileNotFoundException.class);
    }

    @Test
    @DisplayName("should return list of arrays of strings in csv file")
    void testReadCSV() throws IOException {
        String[] lines = {"name, age", "John, 20", "Mary, 21"};
        List<String[]> expectedLines = Arrays.asList(
                new String[]{"name", "age"},
                new String[]{"John", "20"},
                new String[]{"Mary", "21"}
        );
        Path path = fs.getPath("test/file");
        Files.write(path, Arrays.asList(lines));

        List<String[]> readLines = fileManager.readCSV(path);

        assertThat(readLines).containsExactly(expectedLines.toArray(new String[0][0]));
    }

    @Test
    @DisplayName("should throw FileNotFoundException if file does not exist")
    void testReadCSVOfNonExistingFile() {
        Path path = fs.getPath("test/nonExistingFile");

        assertThatThrownBy(() -> fileManager.readCSV(path))
                .isInstanceOf(FileNotFoundException.class);
    }

    @Test
    @DisplayName("should overwrite file if it exists")
    void testOverwriteFile1() throws IOException {
        Path path = fs.getPath("test/file");
        String content = "content";
        Files.createFile(path);
        Files.write(path, "old content".getBytes());

        fileManager.writeFile(path, content, false);

        assertThat(path).exists().hasContent(content);
    }

    @Test
    @DisplayName("should create file if it does not exist")
    void testOverwriteFile2() {
        Path path = fs.getPath("test/file");
        String content = "content";

        fileManager.writeFile(path, content, false);

        assertThat(path).exists().hasContent(content);
    }

    @Test
    @DisplayName("should append to file if file exists")
    void testAppendToFile1() throws IOException {
        Path path = fs.getPath("test/file");
        String content = "content";
        Files.createFile(path);

        fileManager.writeFile(path, content, true);

        assertThat(path).exists().hasContent(content);

        fileManager.writeFile(path, content, true);

        assertThat(path).exists().hasContent(content + content);
    }

    @Test
    @DisplayName("should create file if it does not exist")
    void testAppendToFile2() {
        Path path = fs.getPath("test/file");
        String content = "content";

        fileManager.writeFile(path, content, true);

        assertThat(path).exists().hasContent(content);
    }

    @Test
    @DisplayName("should write csv to file and create file if it does not exist")
    void testWriteCSV() {
        Path path = fs.getPath("test/file");
        List<String[]> lines = Arrays.asList(
                new String[]{"name", "age"},
                new String[]{"John", "20"},
                new String[]{"Mary", "21"}
        );
        String expectedLines = "name,age\n" +
                "John,20\n" +
                "Mary,21\n";

        fileManager.writeCSV(path, lines);

        assertThat(path).exists().hasContent(expectedLines);
    }

    @Test
    @DisplayName("should write csv to file and overwrite file if it exists")
    void testWriteCSV2() throws IOException {
        Path path = fs.getPath("test/file");
        List<String[]> lines = Arrays.asList(
                new String[]{"name", "age"},
                new String[]{"John", "20"},
                new String[]{"Mary", "21"}
        );
        String expectedLines = "name,age\n" +
                "John,20\n" +
                "Mary,21\n";
        Files.createFile(path);
        Files.write(path, "old content".getBytes());

        fileManager.writeCSV(path, lines);

        assertThat(path).exists().hasContent(expectedLines);
    }

    @Test
    @DisplayName("should append csv to file and create file if it does not exist")
    void testAppendCSV() {
        Path path = fs.getPath("test/file");
        String[] lines = new String[]{"name", "age"};
        String expectedLines = "name,age\n";

        fileManager.appendToCSV(path, lines);

        assertThat(path).exists().hasContent(expectedLines);
    }

    @Test
    @DisplayName("should append csv to file and append to file if it exists")
    void testAppendCSV2() throws IOException {
        Path path = fs.getPath("test/file");
        String oldContent = "old,content\n";
        String[] lineToWrite = new String[]{"name", "age"};
        String expectedLines = oldContent + "name,age\n";
        Files.createFile(path);
        Files.write(path, oldContent.getBytes());

        fileManager.appendToCSV(path, lineToWrite);

        assertThat(path).exists().hasContent(expectedLines);
    }

    @Test
    @DisplayName(("should list all directories in given directory if it exists"))
    void testListDirectories() throws DirectoryNotFoundException, IOException {
        String[] directories = {"dir1", "dir2", "dir3"};
        Path path = fs.getPath("test");
        for (String dir : directories) {
            Files.createDirectory(path.resolve(dir));
        }

        String[] listedDirectories = fileManager.listDirectories(Config.absolutePath());

        assertThat(listedDirectories).containsExactlyElementsOf(Arrays.asList(directories));
    }

    @Test
    @DisplayName("should throw DirectoryNotFoundException if directory does not exist")
    void testListDirectoriesOfNonExistingDirectory() {
        Path path = fs.getPath(NON_EXISTING_DIRECTORY);
        assertThatThrownBy(() -> fileManager.listDirectories(path))
                .isInstanceOf(DirectoryNotFoundException.class);
    }

    @Test
    @DisplayName("should list all files in given directory if it exists")
    void testListFiles() throws DirectoryNotFoundException, IOException {
        String[] files = {"file1.txt", "file2.mp2", "file3.pdf"};
        String[] expectedFiles = {"file1", "file2", "file3"};
        Path path = fs.getPath("test");
        for (String file : files) {
            Files.createFile(path.resolve(file));
        }

        String[] listedFiles = fileManager.listFiles(path);

        assertThat(listedFiles).containsExactlyElementsOf(Arrays.asList(expectedFiles));
    }

    @Test
    @DisplayName("should throw DirectoryNotFoundException if directory does not exist")
    void testListFilesOfNonExistingDirectory() {
        Path path = fs.getPath(NON_EXISTING_DIRECTORY);
        assertThatThrownBy(() -> fileManager.listFiles(path))
                .isInstanceOf(DirectoryNotFoundException.class);
    }

    @Test
    void testListWorkspaces() throws DirectoryNotFoundException, DirectoryAlreadyExistsException {
        for (String workspace : WORKSPACES) {
            fileManager.createDirectory(Config.workspacePath(workspace));
        }
        String[] listedWorkspaces = fileManager.listWorkspaces();
        Arrays.sort(listedWorkspaces);
        Assertions.assertArrayEquals(WORKSPACES, listedWorkspaces);
    }

    @Test
    void testListDatabases() throws DirectoryNotFoundException, DirectoryAlreadyExistsException {
        for (String database : DATABASES) {
            fileManager.createDirectory(Config.databasePath(WORKSPACES[0], database));
        }
        String[] listedDatabases = fileManager.listDatabases(WORKSPACES[0]);
        Arrays.sort(listedDatabases);
        Assertions.assertArrayEquals(DATABASES, listedDatabases);
    }

    @Test
    void testListTables() throws DirectoryNotFoundException, DatabaseAlreadyExistException, IOException {
        fileManager.createDatabase(WORKSPACES[0], DATABASES[0]);
        for (String table : TABLES) {
            Files.createFile(Config.tablePath(WORKSPACES[0], DATABASES[0], table));
        }
        String[] listedTables = fileManager.listTables(WORKSPACES[0], DATABASES[0]);
        Arrays.sort(listedTables);
        Assertions.assertArrayEquals(TABLES, listedTables);
    }

    @Test
    void testReadSchemaFile() throws IOException, DatabaseAlreadyExistException {
        fileManager.createDatabase(WORKSPACES[0], DATABASES[0]);
        fileManager.appendLineToFile(Config.schemaPath(WORKSPACES[0], DATABASES[0]), "schema");
        List<String> lines = fileManager.readSchemaFile(WORKSPACES[0], DATABASES[0]);
        Assertions.assertEquals(1, lines.size());
        Assertions.assertEquals("schema", lines.get(0));
    }

    @Test
    void testReadSchemaFileOfNonExistingDatabase() throws IOException {
        Files.createDirectories(Config.databasePath(WORKSPACES[0], DATABASES[0]));
        Assertions.assertThrows(FileNotFoundException.class, () -> {
            fileManager.readSchemaFile(WORKSPACES[0], DATABASES[0]);
        });
    }
}
