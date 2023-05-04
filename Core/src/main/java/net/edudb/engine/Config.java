/*
 *
 * EduDB is made available under the OSI-approved MIT license.
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 * /
 */

package net.edudb.engine;

import net.edudb.block.BlockFileType;
import net.edudb.structure.table.TableFileType;

import java.io.File;
import java.nio.file.Path;

/**
 * Stores the system's configuration.
 *
 * @author Ahmed Abdul Badie
 */
public class Config {
    public static final int PAGE_SIZE = 100;
    public static final int BUFFER_SIZE = 1;
    public static final int MAX_REQUESTS_NUMBER_PER_WORKSPACE = 1000;
    public static final long DURATION_OF_REQUESTS_LIMIT_IN_SECONDS = (long) 60 * 60 * 24; // 1 day

    private static Path absolutePath;

    private Config() {
        throw new IllegalStateException("Utility class");
    }

    private static final ThreadLocal<String> currentDatabaseName = new ThreadLocal<>();
    private static final ThreadLocal<String> currentWorkspace = new ThreadLocal<>();

    public static void setCurrentDatabaseName(String name) {
        currentDatabaseName.set(name);
    }

    public static String getCurrentDatabaseName() {
        return currentDatabaseName.get();
    }

    public static void setCurrentWorkspace(String name) {
        currentWorkspace.set(name);
    }

    public static String getCurrentWorkspace() {
        return currentWorkspace.get();
    }

    public static void cleanThreadLocal() {
        currentDatabaseName.remove();
        currentWorkspace.remove();
    }

    /**
     * @return The type of the block file to save to disk.
     */
    public static BlockFileType blockType() {
        return BlockFileType.Binary;
    }

    /**
     * @return The type of the table file to save to disk.
     */
    public static TableFileType tableType() {
        return TableFileType.Binary;
    }

    /**
     * @return The system's absolute path on disk.
     */
    public static Path absolutePath() {
        if (absolutePath == null) {
            return new File("data").toPath();
        }
        return absolutePath;
    }

    public static void setAbsolutePath(Path path) {
        absolutePath = path;
    }

    public static Path adminsPath() {
        return absolutePath().resolve("admins.csv");
    }

    public static Path usersPath(String workspaceName) {
        return workspacePath(workspaceName).resolve("users.csv");
    }

    // ======================================== WORKSPACES ========================================

    public static Path workspacesPath() {
        return absolutePath().resolve("workspaces");
    }

    public static Path workspacePath(String workspaceName) {
        return workspacesPath().resolve(workspaceName);
    }

    // ======================================== DATABASES ========================================


    public static Path databasesPath(String workspaceName) {
        return workspacePath(workspaceName).resolve("databases");
    }

    /**
     * @return The path to the current open database. Null if no database is
     * currently open.
     */
    public static Path databasePath(String workspaceName, String databaseName) {
        return databasesPath(workspaceName).resolve(databaseName);
    }

    // ======================================== SCHEMAS ========================================

    public static Path schemaPath(String workspaceName, String databaseName) {
        return databasePath(workspaceName, databaseName).resolve("schema.txt");
    }

    // ======================================== TABLES ========================================


    /**
     * @return The path to the table files on disk.
     */
    public static Path tablesPath(String workspaceName, String databaseName) {
        return databasePath(workspaceName, databaseName).resolve("tables");
    }

    public static Path tablePath(String workspaceName, String databaseName, String tableName) {
        return tablesPath(workspaceName, databaseName).resolve(tableName + ".table");
    }

    // ======================================== PAGES ========================================

    /**
     * @return The path to the page files on disk.
     */
    public static Path pagesPath(String workspaceName, String databaseName) {
        return databasePath(workspaceName, databaseName).resolve("blocks");
    }

    public static Path pagePath(String workspaceName, String databaseName, String pageName) {
        return pagesPath(workspaceName, databaseName).resolve(pageName + ".block");
    }

    // ======================================== INDEXES ========================================
    public static Path indexesPath(String workspaceName, String databaseName) {
        return databasePath(workspaceName, databaseName).resolve("indexes");
    }

    public static Path indexPath(String workspaceName, String databaseName, String tableName, String columnName) {
        String indexName = getIndexName(tableName, columnName);
        return indexesPath(workspaceName, databaseName).resolve(indexName + ".index");
    }


    public static Path indexPath(String workspaceName, String databaseName, String indexName) {
        return indexesPath(workspaceName, databaseName).resolve(indexName + ".index");
    }

    public static String getIndexName(String tableName, String columnName) {
        return tableName + "_" + columnName;
    }
}
