/*
 *
 * EduDB is made available under the OSI-approved MIT license.
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 * /
 */

package net.edudb.index;

import net.edudb.data_type.DataType;
import net.edudb.engine.Config;
import net.edudb.engine.FileManager;
import net.edudb.exception.DatabaseNotFoundException;
import net.edudb.exception.IndexAlreadyExistException;
import net.edudb.exception.IndexNotFoundException;
import net.edudb.relation.Relation;
import net.edudb.relation.RelationIterator;
import net.edudb.relation.VolatileRelation;
import net.edudb.structure.Record;
import net.edudb.structure.table.Table;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class IndexManager {
    private final FileManager fileManager;
    private final Map<String, Map<String, Map<String, Map<String, Index<DataType>>>>> indexes;

    public IndexManager() {
        fileManager = FileManager.getInstance();
        indexes = new HashMap<>();
    }

    public void addIndexToMemory(String workspace, String databaseName,
                                 String tableName, String columnName, Index<DataType> index) {
        indexes.putIfAbsent(workspace, new HashMap<>());
        indexes.get(workspace).putIfAbsent(databaseName, new HashMap<>());
        indexes.get(workspace).get(databaseName).putIfAbsent(tableName, new HashMap<>());

        indexes.get(workspace).get(databaseName).get(tableName).put(columnName, index);
    }

    public void removeIndexFromMemory(String workspace, String databaseName,
                                      String tableName, String columnName) {
        indexes.get(workspace).get(databaseName).get(tableName).remove(columnName);
    }


    public void loadDatabaseIndices(String workspaceName, String databaseName) throws DatabaseNotFoundException {
        indexes.putIfAbsent(workspaceName, new HashMap<>());
        indexes.get(workspaceName).putIfAbsent(databaseName, new HashMap<>());
        String[][] indices = fileManager.readIndices(workspaceName, databaseName);
        for (String[] index : indices) {
            String tableName = index[0];
            String columnName = index[1];
            File indexFile = Config.indexPath(workspaceName, databaseName, tableName, columnName).toFile();
            Index<DataType> indexObject = new BtreeIndex<>(indexFile);
            addIndexToMemory(workspaceName, databaseName, tableName, columnName, indexObject);
        }
    }

    public void offloadDatabaseIndices(String workspaceName, String databaseName) {
        if (!indexes.containsKey(workspaceName) || !indexes.get(workspaceName).containsKey(databaseName)) {
            return;
        }
        indexes.get(workspaceName).remove(databaseName);
    }

    public void offloadTableIndices(String workspaceName, String databaseName, String tableName) {
        if (!indexes.containsKey(workspaceName) || !indexes.get(workspaceName).containsKey(databaseName) ||
                !indexes.get(workspaceName).get(databaseName).containsKey(tableName)) {
            return;
        }
        indexes.get(workspaceName).get(databaseName).remove(tableName);
    }

    public void flushDatabaseIndices(String workspaceName, String databaseName) {
        Map<String, Map<String, Index<DataType>>> databaseIndices = indexes.get(workspaceName).get(databaseName);
        for (Map.Entry<String, Map<String, Index<DataType>>> tableIndices : databaseIndices.entrySet()) {
            for (Map.Entry<String, Index<DataType>> columnIndices : tableIndices.getValue().entrySet()) {
                columnIndices.getValue().flush();
            }
        }
    }

    public void flushAllIndices() {
        for (Map.Entry<String, Map<String, Map<String, Map<String, Index<DataType>>>>> workspaceIndices : indexes.entrySet()) {
            for (Map.Entry<String, Map<String, Map<String, Index<DataType>>>> databaseIndices : workspaceIndices.getValue().entrySet()) {
                flushDatabaseIndices(workspaceIndices.getKey(), databaseIndices.getKey());
            }
        }
    }

    public void createIndex(String workspace, String databaseName,
                            Table table, String columnName) throws IndexAlreadyExistException {
        if (getIndex(workspace, databaseName, table.getName(), columnName).isPresent()) {
            throw new IndexAlreadyExistException("Index already exists.", new Throwable("Index already exists."));
        }
        File indexFile = fileManager.createIndex(workspace, databaseName, table.getName(), columnName);
        Index<DataType> index = new BtreeIndex<>(indexFile);

        addIndexToMemory(workspace, databaseName, table.getName(), columnName, index);

        Relation relation = new VolatileRelation(table);
        RelationIterator relationIterator = relation.getIterator();
        while (relationIterator.hasNext()) {
            String pageName = relationIterator.getCurrentPage().getName();
            Record currentRecord = relationIterator.next();
            DataType data = currentRecord.getValue(columnName);
            index.insert(data, pageName);
        }
    }

    public void dropIndex(String workspace, String databaseName, String tableName, String columnName)
            throws IndexNotFoundException {
        fileManager.deleteIndex(workspace, databaseName, tableName, columnName);
        removeIndexFromMemory(workspace, databaseName, tableName, columnName);
    }

    public void dropTableIndices(String workspace, String databaseName, String tableName) {
        Map<String, Index<DataType>> tableIndices = indexes.get(workspace).get(databaseName).get(tableName);
        for (Map.Entry<String, Index<DataType>> columnIndices : tableIndices.entrySet()) {
            try {
                fileManager.deleteIndex(workspace, databaseName, tableName, columnIndices.getKey());
            } catch (IndexNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
        offloadTableIndices(workspace, databaseName, tableName);
    }

    public Optional<Index<DataType>> getIndex(String workspace, String databaseName, String tableName, String columnName) {
        if (!indexes.containsKey(workspace) || !indexes.get(workspace).containsKey(databaseName) ||
                !indexes.get(workspace).get(databaseName).containsKey(tableName) ||
                !indexes.get(workspace).get(databaseName).get(tableName).containsKey(columnName)) {
            return Optional.empty();
        }
        return Optional.of(indexes.get(workspace).get(databaseName).get(tableName).get(columnName));
    }

}
