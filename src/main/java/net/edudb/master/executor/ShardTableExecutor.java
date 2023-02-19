/*
EduDB is made available under the OSI-approved MIT license.

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

package net.edudb.master.executor;

import net.edudb.data_type.DataType;
import net.edudb.data_type.VarCharType;
import net.edudb.engine.Utility;
import net.edudb.master.MasterWriter;
import net.edudb.meta_manager.MetaDAO;
import net.edudb.meta_manager.MetaManager;
import net.edudb.metadata_buffer.MetadataBuffer;
import net.edudb.response.Response;

import java.util.Hashtable;
import java.util.regex.Matcher;

/**
 * Sets the distribution method of a table to sharding
 * and sets the distribution column
 *
 * @author Fady Sameh
 */
public class ShardTableExecutor implements MasterExecutorChain {

    private MasterExecutorChain nextElement;
    private String regex = "\\A(?:(?i)shard)\\s+(?:(?i)table)\\s+\\((\\w+), (\\w+)\\)\\s*;?\\z";

    @Override
    public void setNextElementInChain(MasterExecutorChain chainElement) {
        this.nextElement = chainElement;
    }

    public void execute(String string) {
        if (string.toLowerCase().startsWith("shard table")) {
            Matcher matcher = Utility.getMatcher(string, regex);
            if (matcher.matches()) {
                String tableName = matcher.group(1);
                String distributionColumn = matcher.group(2);

                Hashtable<String, DataType> table = MetadataBuffer.getInstance().getTables().get(tableName);

                if (table == null) {
                    MasterWriter.getInstance().write(new Response("Table '" + tableName + "' does not exist"));
                    return;
                }



                    /**
                     * checking that distribution method is not already set
                     */

                        String distributionMethod = ((VarCharType) table.get("distribution_method")).getString();
                        if (!distributionMethod.equals("null")) {
                            MasterWriter.getInstance().write(new Response("Distribution method for table '" + tableName
                                    + "' has already been set"));
                            return;
                        }


                    /**
                     * checking that distribution column is an existing column
                     */

                        String metadata = ((VarCharType) table.get("metadata")).getString();
                        String[] tokens = metadata.split(" ");
                        boolean isExistingColumn = false;

                        for (int i = 0; i < tokens.length; i+=2)
                            if (tokens[i].equals(distributionColumn)) {
                                isExistingColumn = true;
                                break;
                            }

                        if (!isExistingColumn) {
                            MasterWriter.getInstance().write(new Response("Column '" + distributionColumn +"' does not exist in table '"
                            + tableName + "'"));
                            return;
                        }



                MetaDAO metaDAO = MetaManager.getInstance();
                metaDAO.editTable(tableName, "sharding", distributionColumn, -1);

            }
        }
        else {
            nextElement.execute(string);
        }
    }
}
