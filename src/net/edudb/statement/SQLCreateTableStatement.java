/*
EduDB is made available under the OSI-approved MIT license.

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

package net.edudb.statement;

import gudusoft.gsqlparser.TCustomSqlStatement;
import gudusoft.gsqlparser.nodes.TColumnDefinitionList;
import gudusoft.gsqlparser.stmt.TCreateTableSqlStatement;

/**
 * Holds information about the SQL CREATE TABLE statement.
 * 
 * @author Ahmed Abdul Badie
 *
 */
public class SQLCreateTableStatement extends SQLStatement {

	/**
	 * <b>ATTENTION</b><br>
	 * <br>
	 * 
	 * Do not access `statement` from concurrent threads as it will cause
	 * exceptions.
	 */
	private TCreateTableSqlStatement statement;
	private String columnList;
	private String tableName;
	private String statementString;
	private String[] columnNames;
	private String[] dataTypeNames;

	public SQLCreateTableStatement(TCustomSqlStatement tCustomSqlStatement) {
		this.statement = (TCreateTableSqlStatement) tCustomSqlStatement;
		this.columnList = statement.getColumnList().toString();
		this.tableName = statement.getTargetTable().toString();
		this.statementString = statement.toString();

		TColumnDefinitionList columnList = this.statement.getColumnList();
		this.columnNames = new String[columnList.size()];
		this.dataTypeNames = new String[columnList.size()];
		for (int i = 0; i < columnList.size(); i++) {
			this.columnNames[i] = columnList.getColumn(i).getColumnName().toString();
			this.dataTypeNames[i] = columnList.getColumn(i).getDatatype().toString();
		}
	}

	/**
	 * 
	 * @return String containing a list of column names with their types.
	 */
	public String getColumnList() {
		return columnList;
	}

	/**
	 * 
	 * @return List of column names.
	 */
	public String[] getColumnNames() {
		return columnNames;
	}

	/**
	 * 
	 * @return List of data type names.
	 */
	public String[] getDataTypeNames() {
		return dataTypeNames;
	}

	@Override
	public String getTableName() {
		return tableName;
	}

	@Override
	public SQLStatementType statementType() {
		return SQLStatementType.SQLCreateTableStatement;
	}

	@Override
	public String toString() {
		return statementString;
	}

}
