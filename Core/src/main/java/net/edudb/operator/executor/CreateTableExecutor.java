/*
 *
 * EduDB is made available under the OSI-approved MIT license.
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 * /
 */

package net.edudb.operator.executor;

import net.edudb.data_type.DataType;
import net.edudb.engine.Config;
import net.edudb.engine.DatabaseEngine;
import net.edudb.exception.DatabaseNotFoundException;
import net.edudb.exception.TableAlreadyExistException;
import net.edudb.exception.WorkspaceNotFoundException;
import net.edudb.operator.CreateTableOperator;
import net.edudb.operator.Operator;
import net.edudb.relation.Relation;
import net.edudb.relation.VolatileRelation;
import net.edudb.statement.SQLCreateTableStatement;
import net.edudb.structure.table.Table;

import java.util.LinkedHashMap;

/**
 * Executes the SQL CREATE TABLE statement.
 *
 * @author Ahmed Abdul Badie
 */
public class CreateTableExecutor extends PostOrderOperatorExecutor implements OperatorExecutionChain {
    private OperatorExecutionChain nextElement;

    @Override
    public void setNextElementInChain(OperatorExecutionChain chainElement) {
        this.nextElement = chainElement;
    }

    @Override
    public Relation execute(Operator operator) {
        if (!(operator instanceof CreateTableOperator)) {
            return nextElement.execute(operator);
        }

        SQLCreateTableStatement statement = (SQLCreateTableStatement) operator.getParameter();
        boolean executable = true;

        String[] columnNames = statement.getColumnNames();
        String[] dataTypeNames = statement.getDataTypeNames();
        LinkedHashMap<String, String> columnTypes = new LinkedHashMap<>();
        for (int i = 0; i < columnNames.length; i++) {
            if (DataType.isSupported(dataTypeNames[i])) {
                columnTypes.put(columnNames[i], dataTypeNames[i]);
            } else {
                System.err.println("Type '" + dataTypeNames[i] + "' is not a supported type");
                executable = false;
            }
        }

        if (!executable) {
            return null;
        }


        String tableSchemaLine = statement.getTableName();
        tableSchemaLine += " " + statement.getColumnList();


        String workspaceName = Config.getCurrentWorkspace();
        String databaseName = Config.getCurrentDatabaseName();

        try {
            Table table = DatabaseEngine.getInstance().createTable(workspaceName, databaseName, tableSchemaLine, columnTypes);
            return new VolatileRelation(table);
        } catch (TableAlreadyExistException | DatabaseNotFoundException | WorkspaceNotFoundException e) {
            throw new RuntimeException(e);
        }

    }

}
