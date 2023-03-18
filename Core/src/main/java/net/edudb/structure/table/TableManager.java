/*
 *
 * EduDB is made available under the OSI-approved MIT license.
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 * /
 */

package net.edudb.structure.table;

import net.edudb.engine.Config;
import net.edudb.engine.FileManager;
import net.edudb.statistics.Schema;

import java.io.File;
import java.util.HashMap;

/**
 * TableManager is a singleton that is a wrapper class around
 * TableAbstractFactory. It handles the reading and writing of tables by
 * contacting the File Manager.
 *
 * @author Ahmed Abdul Badie
 */
public class TableManager { //TODO: revise the design

    private static final TableManager instance = new TableManager();
    /**
     * tableBuffer is a map [workspaceName, [databaseName, [tableName, table]]]
     */
    private final HashMap<String, HashMap<String, HashMap<String, Table>>> tableBuffer;

    private TableManager() {
        this.tableBuffer = new HashMap<>();
    }

    public static TableManager getInstance() {
        return instance;
    }

    /**
     * Reads a table from the disk if it is not available in the table buffer
     * pool.
     *
     * @param tableName Name of the table to read.
     * @return The read table.
     * @deprecated Use {@link TableManager#readTable(String, String, String)} instead.
     */
    @Deprecated
    public synchronized Table read(String tableName) {
        String workspaceName = Config.getCurrentWorkspace();
        String databaseName = Config.getCurrentDatabaseName();

        return readTable(workspaceName, databaseName, tableName);
    }

    public synchronized Table readTable(String workspaceName, String databaseName, String tableName) {
        tableBuffer.putIfAbsent(workspaceName, new HashMap<>());
        tableBuffer.get(workspaceName).putIfAbsent(databaseName, new HashMap<>());

        Table table;

        table = tableBuffer.get(workspaceName).get(databaseName).get(tableName);

        if (table != null) {
            return table;
        }

        table = FileManager.getInstance().readTable(tableName);

        tableBuffer.get(workspaceName).get(databaseName).put(tableName, table);

        return table;
    }

    /**
     * Adds the table to the buffer pool and writes it to disk.
     *
     * @param table The table to write.
     * @deprecated Use {@link TableManager#writeTable(String, String, Table)} instead.
     */
    @Deprecated
    public synchronized void write(Table table) {
        String workspaceName = Config.getCurrentWorkspace();
        String databaseName = Config.getCurrentDatabaseName();

        writeTable(workspaceName, databaseName, table);
    }

    public synchronized void writeTable(String workspaceName, String databaseName, Table table) {
        tableBuffer.putIfAbsent(workspaceName, new HashMap<>());
        tableBuffer.get(workspaceName).putIfAbsent(databaseName, new HashMap<>());
        tableBuffer.get(workspaceName).get(databaseName).put(table.getName(), table);
        FileManager.getInstance().writeTable(workspaceName, databaseName, table);
    }

    /**
     * Writes all the tables to disk. Used when closing the database for the
     * data to be persisted.
     *
     * @deprecated Use {@link TableManager#writeAllTables(String, String)} instead.
     */
    @Deprecated
    public void writeAll() {
        String workspaceName = Config.getCurrentWorkspace();
        String databaseName = Config.getCurrentDatabaseName();

        writeAllTables(workspaceName, databaseName);
    }

    public void writeAllTables(String workspaceName, String databaseName) {
        tableBuffer.putIfAbsent(workspaceName, new HashMap<>());
        tableBuffer.get(workspaceName).putIfAbsent(databaseName, new HashMap<>());

        for (Table table : tableBuffer.get(workspaceName).get(databaseName).values()) {
            writeTable(workspaceName, databaseName, table);
        }

        tableBuffer.get(workspaceName).get(databaseName).clear();
    }

    /**
     * Deletes the table by deleting its pages from disk, removing the table
     * from the schema file, and removing the table from disk.
     *
     * @param table The table to delete.
     * @deprecated Use {@link TableManager#deleteTable(String, String, Table)} instead.
     */
    @Deprecated
    public void delete(Table table) {
        String workspaceName = Config.getCurrentWorkspace();
        String databaseName = Config.getCurrentDatabaseName();

        deleteTable(workspaceName, databaseName, table);
    }

    public void deleteTable(String workspaceName, String databaseName, Table table) {
        tableBuffer.putIfAbsent(workspaceName, new HashMap<>());
        tableBuffer.get(workspaceName).putIfAbsent(databaseName, new HashMap<>());

        table.deletePages();
        //FIXME: use new schema
        // ------------------------------
        String path = Config.tablesPath() + table.getName() + ".table";
        File file = new File(path);

        if (file.exists()) {
            file.delete();
        }

        Schema.getInstance().removeTable(table.getName());
        // ------------------------------

        tableBuffer.get(workspaceName).get(databaseName).remove(table.getName());

    }
}
