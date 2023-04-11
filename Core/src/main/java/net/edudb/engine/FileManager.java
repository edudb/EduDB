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

import java.io.*;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class FileManager {
    private static final FileManager instance = new FileManager();

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
    public ArrayList<String> readFile(String filePath) throws FileNotFoundException {
        ArrayList<String> lines = new ArrayList<>();
        try {
            lines = (ArrayList<String>) Files.readAllLines(Paths.get(filePath));
        } catch (IOException e) {
            throw new FileNotFoundException(String.format("(%s) is not found", filePath));
        }
        lines.removeIf(String::isEmpty);
        return lines;
    }

    public List<String[]> readCSV(String filePath) throws FileNotFoundException {
        ArrayList<String> lines = readFile(filePath);
        List<String[]> data = new ArrayList<>();
        for (String line : lines) {
            data.add(line.split(","));
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
    public void writeFile(String filePath, String data, boolean append) {
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                file.createNewFile();
            }
            BufferedWriter writer = new BufferedWriter(new FileWriter(file, append));
            writer.write(data + System.lineSeparator());
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void writeCSV(String filePath, List<String[]> data) {
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

    public void appendToCSV(String filePath, String[] data) {
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
    public String[] listDirectories(String path) throws DirectoryNotFoundException {
        File directory = new File(path);
        if (!directory.exists()) {
            throw new DirectoryNotFoundException(String.format("(%s) is not found", path));
        }
        File[] subDirectories = directory.listFiles(File::isDirectory);
        String[] subDirectoriesNames = new String[subDirectories.length];
        for (int i = 0; i < subDirectories.length; i++) {
            subDirectoriesNames[i] = subDirectories[i].getName();
        }
        return subDirectoriesNames;
    }

    /**
     * @param path the path to the directory to list
     * @return an array of strings containing the names of the files of the given directory without the extension
     * @throws DirectoryNotFoundException if the given directory does not exist
     */
    public String[] listFiles(String path) throws DirectoryNotFoundException {
        File directory = new File(path);
        if (!directory.exists()) {
            throw new DirectoryNotFoundException(String.format("(%s) is not found", path));
        }
        File[] files = directory.listFiles(File::isFile);
        String[] filesNames = new String[files.length];
        for (int i = 0; i < files.length; i++) {
            String fileName = files[i].getName();
            filesNames[i] = fileName.split("\\.")[0];
        }
        return filesNames;
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
     * @return an array of strings containing the names of the pages in the given database
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
    public void createDirectory(String path) throws DirectoryAlreadyExistsException {
        File directory = new File(path);
        if (directory.exists()) {
            throw new DirectoryAlreadyExistsException(String.format("(%s) already exists", path));
        }
        directory.mkdirs();
    }

    /**
     * @param path the path of the directory to create if not exists
     * @author Ahmed Nasser Gaafar
     */
    public void createDirectoryIfNotExists(String path) {
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
    public void deleteDirectory(String path) throws DirectoryNotFoundException {
        File directory = new File(path);
        if (!directory.exists()) {
            throw new DirectoryNotFoundException(String.format("(%s) is not found", path));
        }
        deleteDirectoryRecursively(directory);
    }

    /**
     * @param directory the directory to delete recursively
     * @author Ahmed Nasser Gaafar
     */
    private void deleteDirectoryRecursively(File directory) {
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    deleteDirectoryRecursively(file);
                } else {
                    file.delete();
                }
            }
        }
        directory.delete();
    }

    private boolean isDirectoryExists(String path) {
        File directory = new File(path);
        return directory.exists();
    }

    public void createFile(String path) throws FileAlreadyExistsException {
        File file = new File(path);
        if (file.exists()) {
            throw new FileAlreadyExistsException(String.format("(%s) already exists", path));
        }
        try {
            file.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void createFileIfNotExists(String path) {
        try {
            createFile(path);
        } catch (FileAlreadyExistsException e) {
            // do nothing
        }
    }

    public void deleteFile(String path) throws FileNotFoundException {
        File file = new File(path);
        if (!file.exists()) {
            throw new FileNotFoundException(String.format("(%s) is not found", path));
        }
        file.delete();
    }

    public void appendLineToFile(String path, String line) throws FileNotFoundException {
        if (!new File(path).exists()) {
            throw new FileNotFoundException(String.format("(%s) is not found", path));
        }
        writeFile(path, line, true);
    }

    public void removeLineFromFileWithPrefix(String path, String prefix) throws FileNotFoundException {
        List<String> lines = readFile(path);
        StringBuilder newFileData = new StringBuilder();
        for (String line : lines) {
            if (line.startsWith(prefix)) continue;
            newFileData.append(line);
        }
        writeFile(path, newFileData.toString(), false);
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
