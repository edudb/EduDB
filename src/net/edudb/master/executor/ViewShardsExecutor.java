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
 * Displays a list of shards of a certain table
 *
 * @author Fady Sameh
 */
public class ViewShardsExecutor implements MasterExecutorChain {

    private MasterExecutorChain nextElement;
    private String regex = "view shards (\\w+)";

    @Override
    public void setNextElementInChain(MasterExecutorChain chainElement) {
        this.nextElement = chainElement;
    }

    @Override
    public void execute(String s) {
        if (s.toLowerCase().startsWith("view shards")) {
            Matcher matcher = Utility.getMatcher(s, regex);
            if (matcher.matches()) {
                String tableName = matcher.group(1);
                if (MetadataBuffer.getInstance().getTables().get(tableName) == null) {
                    MasterWriter.getInstance().write(new Response("Table '" + tableName + "' does not exist"));
                    return;
                }

                String shardsTable =
                        "-----------------------------------------------------------------------------" + "\r\n" +
                                "| ADDRESS                   | ID    | MINIMUM_VALUE     | MAXIMUM_VALUE     |" + "\r\n" +
                                "-----------------------------------------------------------------------------" + "\r\n";

                for (Hashtable<String, DataType> shard: MetadataBuffer.getInstance().getShards().values()) {
                    if (shard.get("table").toString().equals(tableName)) {

                        String address = shard.get("host").toString() + ":" + shard.get("port").toString();
                        shardsTable += "| " + address;
                        for (int i = 0; i < 26 - address.length(); i++) shardsTable += " ";

                        String id = shard.get("id").toString();
                        shardsTable += "| " + id;
                        for (int i = 0; i < 6 - id.length(); i++) shardsTable += " ";

                        String minValue = shard.get("min_value").toString();
                        shardsTable += "| " + minValue;
                        for (int i = 0; i < 18 - minValue.length(); i++) shardsTable += " ";

                        String maxValue = shard.get("max_value").toString();
                        shardsTable += "| " + maxValue;
                        for (int i = 0; i < 18 - maxValue.length(); i++) shardsTable += " ";
                        shardsTable += "|\r\n";
                        shardsTable += "-----------------------------------------------------------------------------\r\n";
                    }
                }

                MasterWriter.getInstance().write(new Response(shardsTable));

            }
        }
        else {
            nextElement.execute(s);
        }
    }
}
