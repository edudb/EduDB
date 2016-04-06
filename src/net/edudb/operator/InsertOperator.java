/*
EduDB is made available under the OSI-approved MIT license.

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

package net.edudb.operator;

import java.util.ArrayList;
import net.edudb.operator.Operator;
import net.edudb.page.DBPage;
import net.edudb.server.ServerWriter;
import net.edudb.statement.SQLInsertStatement;
import net.edudb.statistics.Schema;
import net.edudb.structure.DBRecord;
import net.edudb.table.*;

/**
 * Created by mohamed on 4/11/14.
 */
public class InsertOperator implements Operator {

	/**
	 * @uml.property name="statement"
	 * @uml.associationEnd multiplicity="(1 1)"
	 */
	private SQLInsertStatement statement;

	public InsertOperator(SQLInsertStatement statement) {
		this.statement = statement;
	}

	@Override
	public DBResult execute() {
//		if (!Schema.chekTableExists(statement.getTargetTableName())) {
//			ServerWriter.getInstance().writeln("InsertOperator (execute): " + "Table does not exist");
//			return null;
//		}

		Table table = TableManager.read(statement.getTargetTableName());

		ArrayList<String> values = statement.getValueList();
		DBRecord record = new DBRecord(values, table.getName());

		table.getPageManager().addRecord(record);
		
//		table.getPageManager().print();

		/**
		 * ATTENTION
		 * 
		 * Used to update the table's information on the disk after possibly
		 * attaching a new page to the table's page manager.
		 */
		TableManager.write(table);

		return null;
	}

	@Override
	public DBParameter[] getChildren() {
		return new DBParameter[0];
	}

	@Override
	public void giveParameter(DBParameter par) {

	}

	@Override
	public void runStep(DBPage page) {

	}

	@Override
	public DBPage getPage() {
		return null;
	}

	@Override
	public void print() {

	}

	@Override
	public int numOfParameters() {
		return 0;
	}
}
