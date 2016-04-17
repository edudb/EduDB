/*
EduDB is made available under the OSI-approved MIT license.

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

package net.edudb.statement;

import gudusoft.gsqlparser.TCustomSqlStatement;
import gudusoft.gsqlparser.stmt.TUpdateSqlStatement;

public class SQLUpdateStatement implements SQLStatement {

	private TUpdateSqlStatement statement;
	private String tableName;
	private String statementString;
	private String whereClause;

	public SQLUpdateStatement(TCustomSqlStatement tCustomSqlStatement) {
		this.statement = (TUpdateSqlStatement) tCustomSqlStatement;
		this.tableName = statement.getTargetTable().toString();
		this.statementString = statement.toString();
		if (statement.getWhereClause() != null) {
			this.whereClause = statement.getWhereClause().toString();
		}
	}

	public TUpdateSqlStatement getStatement() {
		return statement;
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
		return SQLStatementType.SQLUpdateStatement;
	}

	@Override
	public String toString() {
		return statementString;
	}

}
