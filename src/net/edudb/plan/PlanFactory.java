/*
EduDB is made available under the OSI-approved MIT license.

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

package net.edudb.plan;

import net.edudb.query.QueryTree;
import net.edudb.server.ServerWriter;
import net.edudb.statement.SQLStatement;
import net.edudb.statement.SQLStatementType;

/**
 * Created by mohamed on 4/1/14.
 */
public class PlanFactory implements Plan {
	/**
	 * @uml.property name="planner"
	 * @uml.associationEnd
	 */
	private Plan planner;

	@Override
	public QueryTree makePlan(SQLStatement statement) {
		setPlanner(statement.statementType());
		if (planner == null) {
			return null;
		}
		QueryTree plan = planner.makePlan(statement);
		return plan;
	}

	public void setPlanner(SQLStatementType statement) {
		ServerWriter.getInstance().writeln("PlanFactory (setPlanner): " + statement);
		switch (statement) {
		case SQLCreateTableStatement:
			planner = new CreateTablePlan();
			break;
		case SQLDeleteStatement:
			planner = new DeletePlan();
			break;
		case SQLInsertStatement:
			planner = new InsertPlan();
			break;
		case SQLSelectStatement:
			planner = new SelectPlan();
			break;
		case SQLUpdateStatement:
			planner = new UpdatePlan();
			break;
		default:
			ServerWriter.getInstance().writeln("Sorry, this statement is not supported.");
		}
	}
}
