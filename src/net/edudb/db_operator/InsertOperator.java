/*
EduDB is made available under the OSI-approved MIT license.

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

package net.edudb.db_operator;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import net.edudb.data_type.DataType;
import net.edudb.data_type.IntegerType;
import net.edudb.db_operator.DBOperator;
import net.edudb.page.DBPage;
import net.edudb.server.ServerWriter;
import net.edudb.statement.SQLInsertStatement;
import net.edudb.statistics.Schema;
import net.edudb.structure.DBColumn;
import net.edudb.structure.TableRecord;
import net.edudb.structure.table.*;
import net.edudb.structure.Record;

/**
 * Created by mohamed on 4/11/14.
 */
public class InsertOperator implements DBOperator {

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
		if (!Schema.getInstance().chekTableExists(statement.getTableName())) {
			ServerWriter.getInstance().writeln("InsertOperator (execute): " + "Table does not exist");
			return null;
		}

		Table table = TableManager.getInstance().read(statement.getTableName());
		ArrayList<DBColumn> columns = Schema.getInstance().getColumns(statement.getTableName());
		ArrayList<String> values = statement.getValueList();
		LinkedHashMap<DBColumn, DataType> data = new LinkedHashMap<>();
		int size = values.size();
		for (int i = 0; i < size; i++) {
			data.put(columns.get(i), new IntegerType(values.get(i)));
		}

		Record record = new TableRecord(data);
		table.addRecord(record);

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
