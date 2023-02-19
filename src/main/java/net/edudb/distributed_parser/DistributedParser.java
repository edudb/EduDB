/*
EduDB is made available under the OSI-approved MIT license.

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

package net.edudb.distributed_parser;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import net.edudb.master.MasterWriter;
import net.edudb.response.Response;
import net.edudb.statement.SQLStatement;
import net.edudb.statement.SQLStatementFactory;

/**
 * This parser is responsible for parsing distributed
 * queries received at the master node
 *
 * @author Fady Sameh
 */
public class DistributedParser {

    TGSqlParser sqlparser;

    public DistributedParser() {
        sqlparser = new TGSqlParser(EDbVendor.dbvpostgresql);
    }

    public SQLStatement parseSQL(String strSQL) {
        sqlparser.setSqltext(strSQL);

        int ret = sqlparser.parse();
        if (ret == 0) {
            SQLStatementFactory statementFactory = new SQLStatementFactory();
            SQLStatement statement = statementFactory.makeSQLStatement(sqlparser.sqlstatements.get(0));

            if (statement == null)
                MasterWriter.getInstance().write(new Response("Unsupported SQL statement"));

            return statement;
        } else {
            MasterWriter.getInstance().write(new Response(sqlparser.getErrormessage()));
            return null;
        }
    }
}
