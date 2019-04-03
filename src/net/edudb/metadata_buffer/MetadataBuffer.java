/*
EduDB is made available under the OSI-approved MIT license.

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

package net.edudb.metadata_buffer;

import net.edudb.data_type.VarCharType;
import net.edudb.master.MasterWriter;
import net.edudb.meta_manager.MetaDAO;
import net.edudb.meta_manager.MetaManager;
import net.edudb.response.Response;
import net.edudb.structure.Column;
import net.edudb.structure.Record;

import java.util.ArrayList;
import java.util.Hashtable;

/**
 * This buffer is used to store results received from the
 * metadata database in the main memory
 *
 * @author Fady Sameh
 */
public class MetadataBuffer {

    private static MetadataBuffer instance = new MetadataBuffer();

    private Hashtable<String, Record> tables = new Hashtable();

    private MetadataBuffer () {}

    public static MetadataBuffer getInstance() { return instance; }

    public Hashtable<String, Record> getTables () {
        if (tables.isEmpty()) {
            MetaDAO metaDAO = MetaManager.getInstance();
            ArrayList<Record> tableRecords = metaDAO.getAll("tables");
            MasterWriter.getInstance().write(new Response("relation", tableRecords, null));
            if (tableRecords != null) {
                for (Record table: tableRecords) {
                    for (Column column: table.getData().keySet()) {
                        if ((column.toString()).equals("name"))
                            tables.put(column.toString(), table);
                    }
                }
            }
        }
        return tables;
    }
}
