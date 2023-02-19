/*
EduDB is made available under the OSI-approved MIT license.

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

package net.edudb.plan;

import net.edudb.operator.CreateTableOperator;
import net.edudb.query.QueryTree;
import net.edudb.server.ServerWriter;
import net.edudb.statement.SQLCreateTableStatement;
import net.edudb.statement.SQLStatement;
import net.edudb.statistics.Schema;

//CREATE TABLE table_name
//(
//column_name1 data_type(size),
//column_name2 data_type(size),
//column_name3 data_type(size),
//....
//);

/**
 * A plan to create a new table in a database.
 * 
 * Created by mohamed on 4/1/14.
 * 
 * @author Ahmed Abdul Badie
 */
public class CreateTablePlan extends Plan {

	@Override
	public QueryTree makePlan(SQLStatement sqlStatement) {
		SQLCreateTableStatement statement = (SQLCreateTableStatement) sqlStatement;
		QueryTree plan = null;
		String tableName = statement.getTableName();

		if (!Schema.getInstance().chekTableExists(statement.getTableName())) {
			CreateTableOperator operator = new CreateTableOperator();
			operator.setParameter(statement);
			plan = new QueryTree(operator);
		} else {
			ServerWriter.getInstance().writeln("Table '" + tableName + "' does exist");
		}
		return plan;
	}

}
