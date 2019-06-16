/*
EduDB is made available under the OSI-approved MIT license.

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

package net.edudb.master.executor;

import net.edudb.data_type.DataType;
import net.edudb.engine.Utility;
import net.edudb.master.MasterWriter;
import net.edudb.metadata_buffer.MetadataBuffer;
import net.edudb.response.Response;

import java.util.Hashtable;
import java.util.regex.Matcher;

/**
 * Shows a table's columns' names and types
 *
 * @author Fady Sameh
 */
public class ShowColumnsExecutor implements MasterExecutorChain {

    private MasterExecutorChain nextElement;
    private String regex = "\\A(?:(?i)show)\\s+(?:(?i)columns)\\s+(?:(?i)from)\\s+(\\w+)\\s*;?\\z";

    @Override
    public void setNextElementInChain(MasterExecutorChain chainElement) {
        this.nextElement = chainElement;
    }

    public void execute(String string) {
        if (string.toLowerCase().startsWith("show columns from")) {
            Matcher matcher = Utility.getMatcher(string, regex);
            if (matcher.matches()) {
                String tableName = matcher.group(1);
                Hashtable<String, DataType> table = MetadataBuffer.getInstance().getTables().get(tableName);

                if (table == null) {
                    MasterWriter.getInstance().write(new Response("Table '" + tableName + "' does not exist"));
                    return;
                }

                String columnsTable =
                        "-------------------------------------------------" + "\r\n" +
                                "| NAME                      | TYPE              |\r\n" +
                                "-------------------------------------------------\r\n";

                String metadata = table.get("metadata").toString();
                String[] metadataArray = metadata.split(" ");

                for (int i = 0; i < metadataArray.length; i+=2) {
                    String columnName = metadataArray[i];
                    String columnType = metadataArray[i+1];

                    columnsTable += "| " + columnName;
                    for (int j = 0; j < 26 - columnName.length(); j++) columnsTable += " ";

                    columnsTable += "| " + columnType;
                    for (int j = 0; j < 18 - columnType.length(); j++) columnsTable += " ";

                    columnsTable += "|\r\n-------------------------------------------------\r\n";

                }

                MasterWriter.getInstance().write(new Response(columnsTable));
            }
        }
        else {
            nextElement.execute(string);
        }
    }
}
