/*
EduDB is made available under the OSI-approved MIT license.

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

package net.edudb.distributed_plan;

import net.edudb.data_type.DataType;
import net.edudb.master.Master;
import net.edudb.master.MasterWriter;
import net.edudb.metadata_buffer.MetadataBuffer;
import net.edudb.distributed_operator.CreateTableOperator;
import net.edudb.distributed_query.QueryTree;
import net.edudb.response.Response;
import net.edudb.statement.SQLCreateTableStatement;
import net.edudb.statement.SQLStatement;
import net.edudb.structure.Record;

import java.util.Hashtable;

public class CreateTablePlan extends DistributedPlan {

    public QueryTree makePlan(SQLStatement sqlStatement) {
        SQLCreateTableStatement statement = (SQLCreateTableStatement) sqlStatement;
        QueryTree plan = null;
        String tableName = statement.getTableName();

        Hashtable<String, Hashtable<String, DataType>> tables = MetadataBuffer.getInstance().getTables();

        if (tables.get(tableName) != null) {
            MasterWriter.getInstance().write(new Response("Table '" + tableName + "' already exists"));
            return null;
        }

        CreateTableOperator operator = new CreateTableOperator();
        operator.setParameter(statement);
        plan = new QueryTree(operator);

        return plan;
    }
}
