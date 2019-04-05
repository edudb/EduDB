/*
EduDB is made available under the OSI-approved MIT license.

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

package net.edudb.distributed_executor;

import net.edudb.data_type.DataType;
import net.edudb.distributed_operator.CreateTableOperator;
import net.edudb.distributed_operator.Operator;
import net.edudb.master.MasterWriter;
import net.edudb.meta_manager.MetaManager;
import net.edudb.response.Response;
import net.edudb.statement.SQLCreateTableStatement;

import java.util.LinkedHashMap;

public class CreateTableExecutor implements OperatorExecutionChain {

    OperatorExecutionChain next;

    public void setNextElementInChain(OperatorExecutionChain chainElement) { this.next = chainElement; }


    public void execute(Operator operator) {
        if (operator instanceof CreateTableOperator) {
            SQLCreateTableStatement statement = (SQLCreateTableStatement) operator.getParameter();

            String[] columnNames = statement.getColumnNames();
            String[] dataTypeNames = statement.getDataTypeNames();
            String tableMetadata = "";
            for (int i = 0; i < columnNames.length; i++) {
                if (DataType.isSupported(dataTypeNames[i])) {
                    tableMetadata += columnNames[i] + " " + dataTypeNames[i];
                    if (i < columnNames.length - 1)
                        tableMetadata += " ";
                } else {
                    MasterWriter.getInstance().writeln(new Response("Type '" + dataTypeNames[i] + "' is not a supported type"));
                    return;
                }
            }
            MasterWriter.getInstance().write(new Response("Table meta data: " + tableMetadata));
            MetaManager.getInstance().writeTable(statement.getTableName(), tableMetadata);
        }
        else {
            next.execute(operator);
        }
    }
}
