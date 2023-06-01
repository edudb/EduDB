/*
 *
 * EduDB is made available under the OSI-approved MIT license.
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 * /
 */

package net.edudb.statistics;

import net.edudb.structure.Column;

import java.util.ArrayList;
import java.util.List;

public class TableSchema {
    private String ownerWorkspaceName;
    private String ownerDatabaseName;
    private String tableName;
    private List<String> columnsNames;
    private List<String> columnsTypes;
    private List<Column> columns;

    public TableSchema(String ownerWorkspaceName, String ownerDatabaseName, String tableSchemaLine) {
        this.ownerWorkspaceName = ownerWorkspaceName;
        this.ownerDatabaseName = ownerDatabaseName;
        this.columnsNames = new ArrayList<>();
        this.columnsTypes = new ArrayList<>();
        this.columns = new ArrayList<>();

        parseTableSchemaLine(tableSchemaLine);
    }

    private void parseTableSchemaLine(String tableSchemaLine) {
        String[] tableSchema = tableSchemaLine.split(" ");
        this.tableName = tableSchema[0];
        for (int i = 1; i < tableSchema.length; i += 2) {
            this.columnsNames.add(tableSchema[i]);
            this.columnsTypes.add(tableSchema[i + 1]);
            columns.add(new Column((i + 1) / 2, tableSchema[i], tableName, tableSchema[i + 1]));
        }
    }

    public String getTableName() {
        return tableName;
    }

    public List<String> getColumnsNames() {
        return columnsNames;
    }

    public List<String> getColumnsTypes() {
        return columnsTypes;
    }

    public List<Column> getColumns() {
        return columns;
    }
}
