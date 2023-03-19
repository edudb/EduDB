/*
 *
 * EduDB is made available under the OSI-approved MIT license.
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 * /
 */

package net.edudb.statistics;

import net.edudb.engine.Config;
import net.edudb.engine.FileManager;
import net.edudb.engine.Utility;
import net.edudb.exception.*;
import net.edudb.structure.Column;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Schema {
    private static volatile Schema instance;
    private Map<String, WorkspaceSchema> workspaces;

    public static Schema getInstance() {
        if (instance == null) {
            synchronized (Schema.class) {
                if (instance == null) {
                    instance = new Schema();
                }
            }
        }
        return instance;
    }

    private Schema() {
        this.workspaces = new HashMap<>();

        String[] workspacesList = new String[0];
        try {
            workspacesList = FileManager.getInstance().listWorkspaces();
        } catch (DirectoryNotFoundException e) {
            Utility.handleDatabaseFileStructureCorruption(e);
        }
        for (String workspace : workspacesList) {
            this.workspaces.put(workspace, null);
        }
    }

    public void reset() {
        this.workspaces = new HashMap<>();
    }

    private void validateWorkspaceExists(String workspaceName) throws WorkspaceNotFoundException {
        if (!containsWorkspace(workspaceName)) {
            throw new WorkspaceNotFoundException(String.format("Workspace %s does not exist", workspaceName));
        }
    }

    private void validateWorkspaceDoesNotExist(String workspaceName) throws WorkspaceAlreadyExistException {
        if (containsWorkspace(workspaceName)) {
            throw new WorkspaceAlreadyExistException(String.format("Workspace %s already exist", workspaceName));
        }
    }

    public void loadWorkspace(String workspaceName) throws WorkspaceNotFoundException {
        validateWorkspaceExists(workspaceName);
        this.workspaces.put(workspaceName, new WorkspaceSchema(workspaceName));
    }

    public void offloadWorkspace(String workspaceName) throws WorkspaceNotFoundException {
        validateWorkspaceExists(workspaceName);
        this.workspaces.put(workspaceName, null);
    }

    private boolean isWorkspaceLoaded(String workspaceName) throws WorkspaceNotFoundException {
        validateWorkspaceExists(workspaceName);
        return this.workspaces.get(workspaceName) != null;
    }

    public WorkspaceSchema getWorkspace(String workspaceName) throws WorkspaceNotFoundException {
        validateWorkspaceExists(workspaceName);

        if (!isWorkspaceLoaded(workspaceName)) loadWorkspace(workspaceName);

        return this.workspaces.get(workspaceName);
    }

    public DatabaseSchema getDatabase(String workspaceName, String databaseName) throws WorkspaceNotFoundException, DatabaseNotFoundException {
        return getWorkspace(workspaceName).getDatabase(databaseName);
    }

    public DatabaseSchema getDatabase(String databaseName) throws WorkspaceNotFoundException, DatabaseNotFoundException {
        return getWorkspace(Config.getCurrentWorkspace()).getDatabase(databaseName);
    }

    public TableSchema getTable(String workspaceName, String databaseName, String tableName) throws WorkspaceNotFoundException, DatabaseNotFoundException, TableNotFoundException {
        return getDatabase(workspaceName, databaseName).getTable(tableName);
    }

    public TableSchema getTable(String tableName) throws WorkspaceNotFoundException, DatabaseNotFoundException, TableNotFoundException {
        return getDatabase(Config.getCurrentWorkspace(), Config.getCurrentDatabaseName()).getTable(tableName);
    }

    public List<String> getTables() {
        try {
            return getDatabase(Config.getCurrentWorkspace(), Config.getCurrentDatabaseName()).getTables().keySet().stream().toList();
        } catch (WorkspaceNotFoundException | DatabaseNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public ArrayList<Column> getColumns(String tableName) {
        try {
            return (ArrayList<Column>) getTable(tableName).getColumns();
        } catch (WorkspaceNotFoundException | DatabaseNotFoundException | TableNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public HashMap<String, ArrayList<String>> getSchema() {
        try {
            return (HashMap<String, ArrayList<String>>) getDatabase(Config.getCurrentWorkspace(), Config.getCurrentDatabaseName()).getSchema();
        } catch (WorkspaceNotFoundException | DatabaseNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean containsWorkspace(String workspaceName) {
        return this.workspaces.containsKey(workspaceName);
    }

    public void addWorkspace(String workspaceName) throws WorkspaceAlreadyExistException {
        validateWorkspaceDoesNotExist(workspaceName);
        this.workspaces.put(workspaceName, null);
    }

    public void removeWorkspace(String workspaceName) throws WorkspaceNotFoundException {
        validateWorkspaceExists(workspaceName);
        this.workspaces.remove(workspaceName);
    }
    
    public boolean checkTableExists(String tableName) {
        return getTables().contains(tableName);
    }

}
