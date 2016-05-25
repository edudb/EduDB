/*
EduDB is made available under the OSI-approved MIT license.

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

package net.edudb.plan;

import net.edudb.operator.InsertOperator;
import net.edudb.operator.Operator;
import net.edudb.operator.parameter.InsertOperatorParameter;
import net.edudb.query.QueryNode;
import net.edudb.query.QueryTree;
import net.edudb.server.ServerWriter;
import net.edudb.statement.SQLInsertStatement;
import net.edudb.statement.SQLStatement;
import net.edudb.statistics.Schema;
import net.edudb.structure.table.Table;
import net.edudb.structure.table.TableManager;

/**
 * A plan to insert data into a table.
 * 
 * Created by mohamed on 4/9/14.
 * 
 * @author Ahmed Abdul Badie
 */
public class InsertPlan extends Plan {

	@Override
	public QueryTree makePlan(SQLStatement sqlStatement) {
		SQLInsertStatement statement = (SQLInsertStatement) sqlStatement;
		String tableName = statement.getTableName();

		if (!Schema.getInstance().chekTableExists(tableName)) {
			ServerWriter.getInstance().writeln("Table '" + tableName + "' does not exist");
			return null;
		}

		Table table = TableManager.getInstance().read(tableName);
		Operator operator = new InsertOperator();
		InsertOperatorParameter parameter = new InsertOperatorParameter(table, statement);
		operator.setParameter(parameter);

		QueryTree tree = new QueryTree((QueryNode) operator);
		return tree;
	}
}
