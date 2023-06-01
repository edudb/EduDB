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
import net.edudb.data_type.DataTypeFactory;
import net.edudb.engine.Config;
import net.edudb.engine.DatabaseEngine;
import net.edudb.exception.InvalidTypeValueException;
import net.edudb.index.Index;
import net.edudb.operator.InsertOperator;
import net.edudb.operator.Operator;
import net.edudb.operator.parameter.InsertOperatorParameter;
import net.edudb.relation.Relation;
import net.edudb.relation.VolatileRelation;
import net.edudb.statement.SQLInsertStatement;
import net.edudb.statistics.Schema;
import net.edudb.structure.Column;
import net.edudb.structure.Record;
import net.edudb.structure.TableRecord;
import net.edudb.structure.table.Table;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Executes the SQL INSERT INTO statement.
 *
 * @author Ahmed Abdul Badie
 */
public class InsertExecutor extends PostOrderOperatorExecutor implements OperatorExecutionChain {
    private OperatorExecutionChain nextElement;

    @Override
    public void setNextElementInChain(OperatorExecutionChain chainElement) {
        this.nextElement = chainElement;
    }

    @Override
    public Relation execute(Operator operator) {
        if (operator instanceof InsertOperator) {
            Operator insert = operator;
            InsertOperatorParameter parameter = (InsertOperatorParameter) insert.getParameter();

            Table table = parameter.getTable();
            SQLInsertStatement statement = parameter.getStatement();

            DataTypeFactory typeFactory = new DataTypeFactory();

            Map<String, String> columnTypes = table.getColumnTypes();

            ArrayList<Column> columns = Schema.getInstance().getColumns(table.getName());
            ArrayList<String> values = statement.getValueList();
            LinkedHashMap<Column, DataType> data = new LinkedHashMap<>();
            int size = values.size();
            for (int i = 0; i < size; i++) {
                try {
                    data.put(columns.get(i),
                            typeFactory.makeType(columnTypes.get(columns.get(i).getName()), values.get(i)));
                } catch (InvalidTypeValueException e) {
                    System.err.println(e.getMessage());
                    e.printStackTrace();
                }

            }

            Record record = new TableRecord(data);

            Relation relation = new VolatileRelation(table);

            // update index if exists
            String workspaceName = Config.getCurrentWorkspace();
            String databaseName = Config.getCurrentDatabaseName();
            String tableName = table.getName();
            String pageName = table.addRecord(record);

            for (int i = 0; i < size; i++) {
                String columnName = columns.get(i).getName();
                Optional<Index<DataType>> indexOptional = DatabaseEngine.getInstance().getIndexManager()
                        .getIndex(workspaceName, databaseName, tableName, columnName);

                if (indexOptional.isPresent()) {
                    Index<DataType> index = indexOptional.get();
                    DataType key = data.get(columns.get(i));
                    index.insert(key, pageName);
                }
            }

            return relation;
        }
        return nextElement.execute(operator);
    }

}
