/*
 *
 * EduDB is made available under the OSI-approved MIT license.
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 * /
 */

package net.edudb.statistics;

import net.edudb.engine.FileManager;
import net.edudb.engine.Utility;
import net.edudb.exception.DatabaseAlreadyExistException;
import net.edudb.exception.DatabaseNotFoundException;
import net.edudb.exception.DirectoryNotFoundException;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class WorkspaceSchema {
    private String workspaceName;
    private Map<String, DatabaseSchema> databases;

    public WorkspaceSchema(String workspaceName) {
        this.workspaceName = workspaceName;
        this.databases = new HashMap<>();

        String[] databasesList = new String[0];
        try {
            databasesList = FileManager.getInstance().listDatabases(workspaceName);
        } catch (DirectoryNotFoundException e) {
            Utility.handleDatabaseFileStructureCorruption(e);
        }
        for (String database : databasesList) {
            this.databases.put(database, null);
        }
    }

    private void validateDatabaseExists(String databaseName) throws DatabaseNotFoundException {
        if (!containsDatabase(databaseName)) {
            throw new DatabaseNotFoundException(String.format("Database %s does not exist", databaseName));
        }
    }

    private void validateDatabaseDoesNotExist(String databaseName) throws DatabaseAlreadyExistException {
        if (containsDatabase(databaseName)) {
            throw new DatabaseAlreadyExistException(String.format("Database %s already exist", databaseName));
        }
    }

    public void loadDatabase(String databaseName) throws DatabaseNotFoundException {
        validateDatabaseExists(databaseName);
        this.databases.put(databaseName, new DatabaseSchema(this.workspaceName, databaseName));
    }

    public void offloadDatabase(String databaseName) throws DatabaseNotFoundException {
        validateDatabaseExists(databaseName);
        this.databases.put(databaseName, null);
    }

    private boolean isDatabaseLoaded(String databaseName) throws DatabaseNotFoundException {
        validateDatabaseExists(databaseName);
        return this.databases.get(databaseName) != null;
    }


    public DatabaseSchema getDatabase(String databaseName) throws DatabaseNotFoundException {
        validateDatabaseExists(databaseName);

        if (!isDatabaseLoaded(databaseName)) loadDatabase(databaseName);

        return databases.get(databaseName);
    }

    public void addDatabase(String databaseName) throws DatabaseAlreadyExistException {
        validateDatabaseDoesNotExist(databaseName);
        databases.put(databaseName, null);
    }

    public void removeDatabase(String databaseName) throws DatabaseNotFoundException {
        validateDatabaseExists(databaseName);
        databases.remove(databaseName);
    }

    public String[] listDatabases() {
        Set<String> databaseNames = databases.keySet();
        return databaseNames.toArray(new String[0]);
    }

    public boolean containsDatabase(String databaseName) {
        return databases.containsKey(databaseName);
    }

    public String getWorkspaceName() {
        return workspaceName;
    }

    public void setWorkspaceName(String workspaceName) {
        this.workspaceName = workspaceName;
    }

    public Map<String, DatabaseSchema> getDatabases() {
        return databases;
    }

    public void setDatabases(Map<String, DatabaseSchema> databases) {
        this.databases = databases;
    }
}
