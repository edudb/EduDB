/*
 *
 * EduDB is made available under the OSI-approved MIT license.
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 * /
 */

package net.edudb.engine;

import net.edudb.buffer.BufferManager;
import net.edudb.engine.authentication.Authentication;
import net.edudb.engine.authentication.UserRole;
import net.edudb.exception.*;
import net.edudb.relation.RelationIterator;
import net.edudb.statistics.DatabaseSchema;
import net.edudb.statistics.Schema;
import net.edudb.statistics.WorkspaceSchema;
import net.edudb.structure.Record;
import net.edudb.structure.table.Table;
import net.edudb.structure.table.TableManager;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class DatabaseEngine {
    private static DatabaseEngine instance = new DatabaseEngine();
    private FileManager fileManager;
    private Schema schema;
    private Map<String, Map<String, Map<String, RelationIterator>>> openedIterators; // <workspace, <database, <uuid, iterator>>
    private ScheduledExecutorService backgroundThread;

    private DatabaseEngine() {
        openedIterators = new HashMap<>();
        createBackgroundThreadToWriteBuffer();
        setupOnCloseHandler();
    }

    private void createBackgroundThreadToWriteBuffer() {
        backgroundThread = Executors.newScheduledThreadPool(1);

        Runnable task = () -> BufferManager.getInstance().writeAll();

        int initialDelay = 0;
        int period = 1;
        backgroundThread.scheduleAtFixedRate(task, initialDelay, period, TimeUnit.MINUTES);
    }

    private void setupOnCloseHandler() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            BufferManager.getInstance().writeAll();
            TableManager.getInstance().writeAllTables();
            backgroundThread.shutdown();
        }));
    }


    public static DatabaseEngine getInstance() {
        return instance;
    }

    public void start() {
        fileManager = FileManager.getInstance();
        initializeDatabase();
        schema = Schema.getInstance();
    }

    private void initializeDatabase() {
        fileManager.createDirectoryIfNotExists(Config.workspacesPath());
        fileManager.createFileIfNotExists(Config.adminsPath());
    }

    public void createUser(String username, String password, UserRole role, String workspace) throws UserAlreadyExistException, WorkspaceNotFoundException {
        Authentication.createUser(username, password, role, workspace);

    }

    public void dropUser(String workspace, String username) throws UserNotFoundException, WorkspaceNotFoundException {
        Authentication.removeUser(workspace, username);
    }

    public void createAdmin(String username, String password) throws UserAlreadyExistException {
        Authentication.createAdmin(username, password);
    }

    public void dropAdmin(String username) throws UserNotFoundException {
        Authentication.removeAdmin(username);
    }

    public void createWorkspace(String workspaceName) throws WorkspaceAlreadyExistException {
        fileManager.createWorkspace(workspaceName);
        schema.addWorkspace(workspaceName);
    }

    public void dropWorkspace(String workspaceName) throws WorkspaceNotFoundException {
        BufferManager.getInstance().removeWorkspace(workspaceName);
        fileManager.deleteWorkspace(workspaceName);
        if (openedIterators.containsKey(workspaceName)) openedIterators.remove(workspaceName);
        if (schema.containsWorkspace(workspaceName)) schema.removeWorkspace(workspaceName);
    }

    public boolean isWorkspaceExist(String workspaceName) {
        return schema.containsWorkspace(workspaceName);
    }


    public void openDatabase(String workspaceName, String databaseName) throws DatabaseNotFoundException, WorkspaceNotFoundException {
        WorkspaceSchema workspaceSchema = schema.getWorkspace(workspaceName);
        workspaceSchema.loadDatabase(databaseName);
    }

    public void closeDatabase(String workspaceName, String databaseName) throws DatabaseNotFoundException, WorkspaceNotFoundException {

        BufferManager.getInstance().writeAll(workspaceName, databaseName);
        TableManager.getInstance().writeAllTables(workspaceName, databaseName);

        WorkspaceSchema workspaceSchema = schema.getWorkspace(workspaceName);
        workspaceSchema.offloadDatabase(databaseName);
    }

    public void createDatabase(String workspaceName, String databaseName) throws DatabaseAlreadyExistException, WorkspaceNotFoundException {
        FileManager.getInstance().createDatabase(workspaceName, databaseName);

        WorkspaceSchema workspaceSchema = schema.getWorkspace(workspaceName);
        if (!workspaceSchema.containsDatabase(databaseName)) workspaceSchema.addDatabase(databaseName);
    }

    public void dropDatabase(String workspaceName, String databaseName) throws DatabaseNotFoundException, WorkspaceNotFoundException {
        FileManager.getInstance().deleteDatabase(workspaceName, databaseName);
        BufferManager.getInstance().removeDatabase(workspaceName, databaseName);
        WorkspaceSchema workspaceSchema = schema.getWorkspace(workspaceName);
        if (openedIterators.containsKey(workspaceName)) {
            Map<String, Map<String, RelationIterator>> workspace = openedIterators.get(workspaceName);
            if (workspace.containsKey(databaseName)) workspace.remove(databaseName);
        }
        if (workspaceSchema.containsDatabase(databaseName)) workspaceSchema.removeDatabase(databaseName);
    }

    public String[] listDatabases(String workspaceName) throws WorkspaceNotFoundException {
        WorkspaceSchema workspaceSchema = schema.getWorkspace(workspaceName);
        return workspaceSchema.listDatabases();
    }

    public Table createTable(String workspaceName, String databaseName, String tableSchemaLine, LinkedHashMap<String, String> columnTypes) throws TableAlreadyExistException, DatabaseNotFoundException, WorkspaceNotFoundException {
        TableManager tableManager = TableManager.getInstance();
        Table table = tableManager.createTable(workspaceName, databaseName, tableSchemaLine, columnTypes);


        WorkspaceSchema workspaceSchema = schema.getWorkspace(workspaceName);
        DatabaseSchema databaseSchema = workspaceSchema.getDatabase(databaseName);
        databaseSchema.addTable(tableSchemaLine);

        return table;
    }

    public void dropTable(String workspaceName, String databaseName, String tableName) throws TableNotFoundException, DatabaseNotFoundException, WorkspaceNotFoundException {
        TableManager tableManager = TableManager.getInstance();
        tableManager.deleteTable(workspaceName, databaseName, tableName);

        WorkspaceSchema workspaceSchema = schema.getWorkspace(workspaceName);
        DatabaseSchema databaseSchema = workspaceSchema.getDatabase(databaseName);
        databaseSchema.removeTable(tableName);
    }

    public void addResultSet(String workspaceName, String databaseName, RelationIterator iterator) {
        openedIterators.putIfAbsent(workspaceName, new HashMap<>());
        Map<String, Map<String, RelationIterator>> workspace = openedIterators.get(workspaceName);

        workspace.putIfAbsent(databaseName, new HashMap<>());
        Map<String, RelationIterator> database = workspace.get(databaseName);

        database.put(iterator.getId(), iterator);
    }

    public void closeResultSet(String workspaceName, String databaseName, String resultSetId) {
        Map<String, Map<String, RelationIterator>> workspace = openedIterators.get(workspaceName);
        Map<String, RelationIterator> database = workspace.get(databaseName);
        database.remove(resultSetId);
    }

    public List<Record> getNextRecord(String workspaceName, String databaseName, String resultSetId, int count) {
        RelationIterator iterator = getIterator(workspaceName, databaseName, resultSetId);
        return iterator.next(count);
    }

    private RelationIterator getIterator(String workspaceName, String databaseName, String uuid) {
        Map<String, Map<String, RelationIterator>> workspace = openedIterators.getOrDefault(workspaceName, new HashMap<>());
        Map<String, RelationIterator> database = workspace.getOrDefault(databaseName, new HashMap<>());
        return database.get(uuid);
    }


}
