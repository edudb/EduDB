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
import net.edudb.engine.FileManager;
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
    private static IndexManager instance;
    private final FileManager fileManager;
    private final Map<String, Map<String, Map<String, Map<String, Index<DataType>>>>> indexes;

    public static IndexManager getInstance() {
        if (instance == null) {
            instance = new IndexManager();
        }
        return instance;
    }


    private IndexManager() {
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

    public void createIndex(String workspace, String databaseName,
                            Table table, String columnName) throws IndexAlreadyExistException {

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

    public Optional<Index<DataType>> getIndex(String workspace, String databaseName, String tableName, String columnName) {
        if (!indexes.containsKey(workspace) || !indexes.get(workspace).containsKey(databaseName) ||
                !indexes.get(workspace).get(databaseName).containsKey(tableName) ||
                !indexes.get(workspace).get(databaseName).get(tableName).containsKey(columnName)) {
            return Optional.empty();
        }
        return Optional.of(indexes.get(workspace).get(databaseName).get(tableName).get(columnName));
    }

    // TODO: take care of open and close
}
