/*
 *
 * EduDB is made available under the OSI-approved MIT license.
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 * /
 */

package net.edudb.server.executor;

import net.edudb.Response;
import net.edudb.console.executor.ConsoleExecutorChain;
import net.edudb.data_type.DataType;
import net.edudb.data_type.DataTypeFactory;
import net.edudb.engine.Utility;
import net.edudb.exception.InvalidTypeValueException;
import net.edudb.server.ServerWriter;
import net.edudb.statistics.Schema;
import net.edudb.structure.Column;
import net.edudb.structure.Record;
import net.edudb.structure.TableRecord;
import net.edudb.structure.table.Table;
import net.edudb.structure.table.TableManager;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.regex.Matcher;

/**
 * Handles the copy command.
 *
 * @author Ahmed Abdul Badie
 */
public class CopyExecutor implements ConsoleExecutorChain {
    private ConsoleExecutorChain nextElement;
    /**
     * Matches strings of the form: <br>
     * <br>
     * <b>COPY table_name DELIMITER 'delimiter';</b><br>
     * <br>
     * and captures <b>table_name</b> and <b>delimiter</b> in the matcher's
     * groups one and two, respectively.
     */
    private final String regex = "^(?:(?i)copy)\\s+(\\D\\w*)\\s+(?:(?i)delimiter)\\s+\\'(.+)\\'\\s*\\;?$";

    @Override
    public void setNextElementInChain(ConsoleExecutorChain chainElement) {
        this.nextElement = chainElement;
    }

    @Override
    public Response execute(String string) {
        /**
         * The string is passed as a command in the first line and data,
         * separated by a delimited, in subsequent lines.
         */
        if (string.toLowerCase().startsWith("copy")) {
            String[] values = string.split("\r\n");
            Matcher matcher = Utility.getMatcher(values[0], regex);
            if (matcher.matches()) {
                String tableName = matcher.group(1);
                if (!Schema.getInstance().checkTableExists(tableName)) {
                    //ServerWriter.getInstance().writeln("Table '" + tableName + "' is not available.");
                    return new Response("Table '" + tableName + "' is not available.");
                }
                Table table = TableManager.getInstance().read(tableName);
                ArrayList<Column> columns = Schema.getInstance().getColumns(tableName);
                DataTypeFactory typeFactory = new DataTypeFactory();

                int count = 0;
                for (int i = 1; i < values.length; i++) {
                    String[] row = values[i].split(matcher.group(2));
                    LinkedHashMap<Column, DataType> data = new LinkedHashMap<>();
                    int size = row.length;
                    for (int j = 0; j < size; j++) {
                        Column column = columns.get(j);
                        try {
                            data.put(column, typeFactory.makeType(column.getTypeName(), row[j]));
                        } catch (InvalidTypeValueException e) {
                            ServerWriter.getInstance().write(new Response(e.getMessage()));
                            e.printStackTrace();
                        }

                    }

                    Record record = new TableRecord(data);
                    table.addRecord(record);
                    ++count;
                }

                //ServerWriter.getInstance().writeln("Copied '" + count + "' records");
                return new Response("Copied '" + count + "' records");
            }
        }
        return nextElement.execute(string);
    }

}
