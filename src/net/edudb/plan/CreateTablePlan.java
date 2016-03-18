/*
EduDB is made available under the OSI-approved MIT license.

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

package net.edudb.plan;

//import gudusoft.gsqlparser.TCustomSqlStatement;
//import gudusoft.gsqlparser.stmt.TCreateTableSqlStatement;
import net.edudb.operator.CreateOperator;
import net.edudb.operator.Operator;
import net.edudb.statement.SQLCreateTableStatement;
import net.edudb.statement.SQLStatement;
import net.edudb.statistics.Schema;

//import java.util.ArrayList;

//CREATE TABLE table_name
//(
//column_name1 data_type(size),
//column_name2 data_type(size),
//column_name3 data_type(size),
//....
//);

/**
 * Created by mohamed on 4/1/14.
 */
public class CreateTablePlan implements Plan {

	@Override
	public Operator makePlan(SQLStatement sqlStatement) {
//		TCreateTableSqlStatement statement = (TCreateTableSqlStatement) tCustomSqlStatement;
		SQLCreateTableStatement statement = (SQLCreateTableStatement) sqlStatement;
		Operator operator = null;
		
		if (!Schema.chekTableExists(statement.getTableName().toString())) {
			operator = new CreateOperator(statement);
//			System.out.println("CreateTablePlan (makePlan): " + statement.getTableName());
		} else {
			System.out.println("CreateTablePlanner (makePlan): " + "table already exists");
		}
		return operator;
	}

}
