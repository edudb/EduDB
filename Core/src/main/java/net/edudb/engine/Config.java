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

    private Config() {
        throw new IllegalStateException("Utility class");
    }

    private static ThreadLocal<String> currentDatabaseName = new ThreadLocal<>();
    private static ThreadLocal<String> currentWorkspace = new ThreadLocal<>();

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
    public static String absolutePath() {
        return System.getProperty("user.dir") + File.separator + "data" + File.separator;
    }

    public static String adminsPath() {
        return absolutePath() + "admins.csv";
    }

    public static String usersPath(String workspaceName) {
        return workspacePath(workspaceName) + "users.csv";
    }

    // ======================================== WORKSPACES ========================================

    public static String workspacesPath() {
        return absolutePath() + "workspaces" + File.separator;
    }

    public static String currentWorkspacePath() {
        return workspacePath(getCurrentWorkspace());
    }

    public static String workspacePath(String workspaceName) {
        return workspacesPath() + workspaceName + File.separator;
    }

    // ======================================== DATABASES ========================================


    public static String databasesPath() {
        return databasesPath(getCurrentWorkspace());
    }

    public static String databasesPath(String workspaceName) {
        return workspacePath(workspaceName) + "databases" + File.separator;
    }

    /**
     * @return The path to the current open database. Null if no database is
     * currently open.
     */
    public static String openedDatabasePath() {
        return databasePath(getCurrentWorkspace(), getCurrentDatabaseName());
    }

    public static String databasePath(String databaseName) {
        return databasePath(getCurrentWorkspace(), databaseName);
    }

    public static String databasePath(String workspaceName, String databaseName) {
        return databasesPath(workspaceName) + databaseName + File.separator;
    }

    // ======================================== SCHEMAS ========================================

    public static String schemaPath() {
        return schemaPath(getCurrentWorkspace(), getCurrentDatabaseName());
    }

    public static String schemaPath(String databaseName) {
        return schemaPath(getCurrentWorkspace(), databaseName);
    }

    public static String schemaPath(String workspaceName, String databaseName) {
        return databasePath(workspaceName, databaseName) + "schema.txt";
    }

    // ======================================== TABLES ========================================


    /**
     * @return The path to the table files on disk.
     */
    public static String tablesPath() {
        return tablesPath(getCurrentWorkspace(), getCurrentDatabaseName());
    }

    public static String tablesPath(String databaseName) {
        return tablesPath(getCurrentWorkspace(), databaseName);
    }

    public static String tablesPath(String workspaceName, String databaseName) {
        return databasePath(workspaceName, databaseName) + "tables" + File.separator;
    }

    public static String tablePath(String tableName) {
        return tablePath(getCurrentWorkspace(), getCurrentDatabaseName(), tableName);
    }

    public static String tablePath(String databaseName, String tableName) {
        return tablePath(getCurrentWorkspace(), databaseName, tableName);
    }

    public static String tablePath(String workspaceName, String databaseName, String tableName) {
        return tablesPath(workspaceName, databaseName) + tableName + ".table";
    }

    // ======================================== PAGES ========================================

    /**
     * @return The path to the page files on disk.
     */
    public static String pagesPath() {
        return pagesPath(getCurrentWorkspace(), getCurrentDatabaseName());
    }

    public static String pagesPath(String databaseName) {
        return pagesPath(getCurrentWorkspace(), databaseName);
    }

    public static String pagesPath(String workspaceName, String databaseName) {
        return databasePath(workspaceName, databaseName) + "blocks" + File.separator;
    }

    public static String pagePath(String workspaceName, String databaseName, String pageName) {
        return pagesPath(workspaceName, databaseName) + pageName + ".block";
    }
}
