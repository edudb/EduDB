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
import net.edudb.exception.TableAlreadyExistException;
import net.edudb.exception.TableNotFoundException;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DatabaseSchema {
    private String ownerWorkspaceName;
    private String databaseName;
    private Map<String, TableSchema> tables;

    public DatabaseSchema(String ownerWorkspace, String databaseName) {
        this.ownerWorkspaceName = ownerWorkspace;
        this.databaseName = databaseName;
        this.tables = new HashMap<>();

        List<String> tablesSchemas = null;
        try {
            tablesSchemas = FileManager.getInstance().readSchemaFile(ownerWorkspace, databaseName);
        } catch (FileNotFoundException e) {
            Utility.handleDatabaseFileStructureCorruption(e);
        }
        for (String tableSchemaLine : tablesSchemas) {
            TableSchema tableSchema = new TableSchema(ownerWorkspace, databaseName, tableSchemaLine);
            this.tables.put(tableSchema.getTableName(), tableSchema);
        }
    }

    private void validateTableExists(String tableName) throws TableNotFoundException {
        if (!containsTable(tableName)) {
            throw new TableNotFoundException(String.format("Table %s does not exist", tableName));
        }
    }

    private void validateTableDoesNotExist(String tableName) throws TableAlreadyExistException {
        if (containsTable(tableName)) {
            throw new TableAlreadyExistException(String.format("Table %s already exists", tableName));
        }
    }


    public TableSchema getTable(String tableName) throws TableNotFoundException {
        validateTableExists(tableName);
        return tables.get(tableName);
    }

    public void addTable(String tableSchemaLine) throws TableAlreadyExistException {
        validateTableDoesNotExist(tableSchemaLine);
        String tableName = tableSchemaLine.split(" ")[0];
        tables.put(tableName, new TableSchema(ownerWorkspaceName, databaseName, tableSchemaLine));
    }

    public void removeTable(String tableName) throws TableNotFoundException {
        validateTableExists(tableName);
        tables.remove(tableName);
    }

    public Map<String, ArrayList<String>> getSchema() {
        Map<String, ArrayList<String>> schema = new HashMap<>();
        for (String tableName : tables.keySet()) {
            schema.put(tableName, (ArrayList) tables.get(tableName).getColumnsNames());
        }
        return schema;
    }

    public boolean containsTable(String tableName) {
        return tables.containsKey(tableName);
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }

    public Map<String, TableSchema> getTables() {
        return tables;
    }

    public void setTables(Map<String, TableSchema> tables) {
        this.tables = tables;
    }

}
