/*
EduDB is made available under the OSI-approved MIT license.

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

package net.edudb.statement;

import java.util.ArrayList;

import gudusoft.gsqlparser.TCustomSqlStatement;
import gudusoft.gsqlparser.nodes.TResultColumnList;
import gudusoft.gsqlparser.stmt.TInsertSqlStatement;

/**
 * Holds information about the SQL INSERT INTO statement.
 * 
 * @author Ahmed Abdul Badie
 *
 */
public class SQLInsertStatement extends SQLStatement {

	/**
	 * <b>ATTENTION</b><br>
	 * <br>
	 * 
	 * Do not access `statement` from concurrent threads as it will cause
	 * exceptions.
	 */
	private TInsertSqlStatement statement;
	private String statementString;
	private String tableName;
	private ArrayList<String> columnList;
	private ArrayList<String> valueList;

	public SQLInsertStatement(TCustomSqlStatement tCustomSqlStatement) {
		this.statement = (TInsertSqlStatement) tCustomSqlStatement;
		this.statementString = statement.toString();
		this.tableName = statement.getTargetTable().toString();
		this.columnList = this.extractColumnList();
		this.valueList = this.extractValueList();
	}

	private ArrayList<String> extractColumnList() {
		if (statement.getColumnList() == null) {
			return null;
		}
		ArrayList<String> columns = new ArrayList<>();
		for (int i = 0; i < statement.getColumnList().size(); i++) {
			columns.add(statement.getColumnList().getObjectName(i).toString());
		}

		return columns;
	}

	/**
	 * 
	 * @return List of column names.
	 */
	public ArrayList<String> getColumnList() {
		return columnList;
	}

	private ArrayList<String> extractValueList() {
		ArrayList<String> values = new ArrayList<>();
		TResultColumnList columnList = statement.getValues().getMultiTarget(0).getColumnList();
		for (int i = 0; i < columnList.size(); i++) {
			values.add(columnList.getResultColumn(i).toString());
		}

		return values;
	}

	/**
	 * 
	 * @return List of values.
	 */
	public ArrayList<String> getValueList() {
		return valueList;
	}

	@Override
	public String getTableName() {
		return tableName;
	}

	@Override
	public SQLStatementType statementType() {
		return SQLStatementType.SQLInsertStatement;
	}

	@Override
	public String toString() {
		return statementString;
	}

}
