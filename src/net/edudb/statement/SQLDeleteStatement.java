/*
EduDB is made available under the OSI-approved MIT license.

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

package net.edudb.statement;

/**
 * Holds information about the SQL DELETE statement.
 * 
 * @author Ahmed Abdul Badie
 *
 */
import gudusoft.gsqlparser.TCustomSqlStatement;
import gudusoft.gsqlparser.stmt.TDeleteSqlStatement;

public class SQLDeleteStatement extends SQLStatement {
	
	/**
	 * <b>ATTENTION</b><br>
	 * <br>
	 * 
	 * Do not access `statement` from concurrent threads as it will cause
	 * exceptions.
	 */
	private TDeleteSqlStatement statement;
	private String tableName;
	private String statementString;
	private String whereClause;

	public SQLDeleteStatement(TCustomSqlStatement tCustomSqlStatement) {
		this.statement = (TDeleteSqlStatement) tCustomSqlStatement;
		this.tableName = statement.getTargetTable().toString();
		this.statementString = statement.toString();
		if (statement.getWhereClause() != null) {
			this.whereClause = statement.getWhereClause().toString();
		}
	}

	public String getWhereClause() {
		return whereClause;
	}

	@Override
	public String getTableName() {
		return tableName;
	}

	@Override
	public SQLStatementType statementType() {
		return SQLStatementType.SQLDeleteStatement;
	}

	@Override
	public String toString() {
		return statementString;
	}

}
