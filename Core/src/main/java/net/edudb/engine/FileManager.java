/*
 *
 * EduDB is made available under the OSI-approved MIT license.
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 * /
 */

package net.edudb.engine;

import net.edudb.block.*;
import net.edudb.exception.*;
import net.edudb.page.Page;
import net.edudb.structure.table.Table;
import net.edudb.structure.table.TableAbstractFactory;
import net.edudb.structure.table.TableReader;
import net.edudb.structure.table.TableReaderFactory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class FileManager {
    private static final FileManager instance = new FileManager();
    private static final String PATH_NOT_FOUND_FORMAT = "(%s) is not found";
    private static final String PATH_ALREADY_EXISTS_FORMAT = "(%s) already exists";

    private FileManager() {
    }

    public static FileManager getInstance() {
        return instance;
    }

    /**
     * Reads all lines from a file and returns them as a list of strings.
     *
     * @param filePath the path to the file to read
     * @return a list of strings containing all lines from the file, or an empty list if an error occurs
     * @author Ahmed Nasser Gaafar
     */
    public List<String> readFile(Path filePath) throws FileNotFoundException {
        if (!isFileExists(filePath)) {
            throw new FileNotFoundException(String.format(PATH_NOT_FOUND_FORMAT, filePath));
        }

        try {
            List<String> lines = Files.readAllLines(filePath);
            lines.removeIf(String::isEmpty);
            return lines;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public List<String[]> readCSV(Path filePath) throws FileNotFoundException {
        List<String> lines = readFile(filePath);
        List<String[]> data = new ArrayList<>();
        for (String line : lines) {
            String[] row = Arrays.stream(line.split(","))
                    .map(String::trim).toArray(String[]::new);

            data.add(row);
        }
        return data;
    }

    /**
     * Writes data to a file.
     *
     * @param filePath the path to the file to write to
     * @param data     the data to write to the file
     * @param append   true to append the data to the end of the file, false to overwrite the file
     * @author Ahmed Nasser Gaafar
     */
    public void writeFile(Path filePath, String data, boolean append) {
        if (!isFileExists(filePath)) {
            try {
                createFile(filePath);
            } catch (FileAlreadyExistsException e) {
                e.printStackTrace();
            }
        }
        try {
            Files.write(filePath, data.getBytes(),
                    append ? StandardOpenOption.APPEND : StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void writeCSV(Path filePath, List<String[]> data) {
        StringBuilder builder = new StringBuilder();
        for (String[] row : data) {
            for (String cell : row) {
                builder.append(cell).append(",");
            }
            builder.deleteCharAt(builder.length() - 1);
            builder.append(System.lineSeparator());
        }

        writeFile(filePath, builder.toString(), false);
    }

    public void appendToCSV(Path filePath, String[] data) {
        StringBuilder builder = new StringBuilder();
        for (String cell : data) {
            builder.append(cell).append(",");
        }
        builder.deleteCharAt(builder.length() - 1);
        builder.append(System.lineSeparator());
        writeFile(filePath, builder.toString(), true);
    }

    /**
     * Reads a page from disk using a block reader.
     *
     * @param pageName The name of the page to read.
     * @return The read page.
     */
    public Page readPage(String workspaceName, String databaseName, String pageName) {
        BlockAbstractFactory blockFactory = new BlockReaderFactory();
        BlockReader blockReader = blockFactory.getReader(Config.blockType());

        try {
            return blockReader.read(workspaceName, databaseName, pageName);
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Writes a page to disk using a block writer.
     *
     * @param page The page to write.
     */
    public void writePage(String workspaceName, String databaseName, Page page) {
        BlockAbstractFactory blockFactory = new BlockWriterFactory();
        BlockWriter blockWriter = blockFactory.getWriter(Config.blockType());

        try {
            blockWriter.write(workspaceName, databaseName, page);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void deletePage(String workspaceName, String databaseName, String pageName) {
        try {
            Files.deleteIfExists(Config.pagePath(workspaceName, databaseName, pageName));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Reads a table from disk using a table reader.
     *
     * @param tableName The name of the table to read.
     * @return The read table.
     */
    public Table readTable(String tableName) {
        TableAbstractFactory tableFactory = new TableReaderFactory();
        TableReader tableReader = tableFactory.getReader(Config.tableType());

        try {
            return tableReader.read(tableName);
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * @param path the path to the directory to list
     * @return an array of strings containing the names of the subdirectories of the given directory
     * @throws DirectoryNotFoundException if the given directory does not exist
     * @author Ahmed Nasser Gaafar
     */
    public String[] listDirectories(Path path) throws DirectoryNotFoundException {
        if (!isDirectoryExists(path)) {
            throw new DirectoryNotFoundException(String.format(PATH_NOT_FOUND_FORMAT, path));
        }

        try (Stream<Path> paths = Files.list(path)) {
            return paths.map(Path::getFileName).map(Path::toString).toArray(String[]::new);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @param path the path to the directory to list
     * @return an array of strings containing the names of the files of the given directory without the extension
     * @throws DirectoryNotFoundException if the given directory does not exist
     */
    public String[] listFiles(Path path) throws DirectoryNotFoundException {
        if (!isDirectoryExists(path)) {
            throw new DirectoryNotFoundException(String.format(PATH_NOT_FOUND_FORMAT, path));
        }

        try (Stream<Path> paths = Files.list(path)) {
            return paths.map(Path::getFileName).map(Path::toString)
                    .map(s -> s.split("\\.")[0]).toArray(String[]::new);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @return an array of strings containing the names of the workspaces
     * @throws DirectoryNotFoundException if the workspaces directory does not exist
     * @author Ahmed Nasser Gaafar
     */
    public String[] listWorkspaces() throws DirectoryNotFoundException {
        return listDirectories(Config.workspacesPath());
    }

    /**
     * @param workspaceName the name of the workspace to list its databases
     * @return an array of strings containing the names of the databases in the given workspace
     * @throws DirectoryNotFoundException if the given workspace does not exist
     * @author Ahmed Nasser Gaafar
     */
    public String[] listDatabases(String workspaceName) throws DirectoryNotFoundException {
        return listDirectories(Config.databasesPath(workspaceName));
    }

    /**
     * @param workspaceName the name of the workspace that contains the database to list its tables
     * @param databaseName  the name of the database to list its tables
     * @return an array of strings containing the names of the tables in the given database
     * @throws DirectoryNotFoundException if the given workspace or database does not exist
     * @author Ahmed Nasser Gaafar
     */
    public String[] listTables(String workspaceName, String databaseName) throws DirectoryNotFoundException {
        return listFiles(Config.tablesPath(workspaceName, databaseName));
    }

    /**
     * @param workspaceName the name of the workspace that contains the database to list its pages
     * @param databaseName  the name of the database to list its pages
     * @return an list of strings containing the names of the pages in the given database
     * @throws FileNotFoundException if the given workspace or database does not exist
     * @author Ahmed Nasser Gaafar
     */
    public List<String> readSchemaFile(String workspaceName, String databaseName) throws FileNotFoundException {
        return readFile(Config.schemaPath(workspaceName, databaseName));
    }

    /**
     * @param path the path of the directory to create
     * @throws DirectoryAlreadyExistsException if the given directory already exists
     * @author Ahmed Nasser Gaafar
     */
    public void createDirectory(Path path) throws DirectoryAlreadyExistsException {
        if (isDirectoryExists(path)) {
            throw new DirectoryAlreadyExistsException(String.format(PATH_ALREADY_EXISTS_FORMAT, path));
        }
        try {
            Files.createDirectories(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @param path the path of the directory to create if not exists
     * @author Ahmed Nasser Gaafar
     */
    public void createDirectoryIfNotExists(Path path) {
        try {
            createDirectory(path);
        } catch (DirectoryAlreadyExistsException e) {
            // do nothing
        }

    }

    /**
     * @param path the path of the directory to delete
     * @throws DirectoryNotFoundException if the given directory does not exist
     * @author Ahmed Nasser Gaafar
     */
    public void deleteDirectory(Path path) throws DirectoryNotFoundException {
        if (!isDirectoryExists(path)) {
            throw new DirectoryNotFoundException(String.format(PATH_NOT_FOUND_FORMAT, path));
        }

        try {
            Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    Files.delete(file);
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    Files.delete(dir);
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private boolean isDirectoryExists(Path path) {
        return Files.exists(path);
    }

    public void createFile(Path path) throws FileAlreadyExistsException {
        if (isFileExists(path)) {
            throw new FileAlreadyExistsException(String.format(PATH_ALREADY_EXISTS_FORMAT, path));
        }
        try {
            Files.createFile(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void createFileIfNotExists(Path path) {
        try {
            createFile(path);
        } catch (FileAlreadyExistsException e) {
            // do nothing
        }
    }

    public void deleteFile(Path path) throws FileNotFoundException {
        if (!isFileExists(path)) {
            throw new FileNotFoundException(String.format(PATH_NOT_FOUND_FORMAT, path));
        }
        try {
            Files.delete(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isFileExists(Path path) {
        return Files.exists(path);
    }

    public void appendLineToFile(Path path, String line) throws FileNotFoundException {
        if (!isFileExists(path)) {
            throw new FileNotFoundException(String.format(PATH_NOT_FOUND_FORMAT, path));
        }
        writeFile(path, line, true);
    }

    public void removeLineFromFileWithPrefix(Path path, String prefix) throws FileNotFoundException {
        if (!isFileExists(path)) {
            throw new FileNotFoundException(String.format(PATH_NOT_FOUND_FORMAT, path));
        }

        try {
            List<String> lines = Files.readAllLines(path);
            List<String> newLines = lines.stream().filter(line -> !line.startsWith(prefix)).toList();
            Files.write(path, newLines);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void createWorkspace(String workspaceName) throws WorkspaceAlreadyExistException {
        try {
            createDirectory(Config.workspacePath(workspaceName));
            createDirectory(Config.databasesPath(workspaceName));
            createFile(Config.usersPath(workspaceName));
        } catch (DirectoryAlreadyExistsException | FileAlreadyExistsException e) {
            throw new WorkspaceAlreadyExistException(String.format("workspace (%s) already exists", workspaceName), e);
        }
    }

    public void deleteWorkspace(String workspaceName) throws WorkspaceNotFoundException {
        try {
            deleteDirectory(Config.workspacePath(workspaceName));
        } catch (DirectoryNotFoundException e) {
            throw new WorkspaceNotFoundException(String.format("workspace (%s) is not found", workspaceName), e);
        }
    }

    public boolean isWorkspaceExists(String workspaceName) {
        return isDirectoryExists(Config.workspacePath(workspaceName));
    }

    public void createDatabase(String workspaceName, String databaseName) throws DatabaseAlreadyExistException {
        try {
            createDirectory(Config.databasePath(workspaceName, databaseName));
            createDirectory(Config.tablesPath(workspaceName, databaseName));
            createDirectory(Config.pagesPath(workspaceName, databaseName));
            createFile(Config.schemaPath(workspaceName, databaseName));
        } catch (DirectoryAlreadyExistsException | FileAlreadyExistsException e) {
            throw new DatabaseAlreadyExistException(String.format("database (%s) already exists", databaseName), e);
        }
    }

    public void deleteDatabase(String workspaceName, String databaseName) throws DatabaseNotFoundException {
        try {
            deleteDirectory(Config.databasePath(workspaceName, databaseName));
        } catch (DirectoryNotFoundException e) {
            throw new DatabaseNotFoundException(String.format("database (%s) is not found", databaseName), e);
        }
    }

}
