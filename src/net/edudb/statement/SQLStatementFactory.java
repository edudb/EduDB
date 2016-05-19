/*
EduDB is made available under the OSI-approved MIT license.

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

package net.edudb.statement;

import gudusoft.gsqlparser.TCustomSqlStatement;

/**
 * A factory that creates SQL statements supported by EduDB.
 * 
 * @author Ahmed Abdul Badie
 *
 */
public class SQLStatementFactory {

	/**
	 * Creates an instance of a supported SQL statement.
	 * 
	 * @param tCustomSqlStatement
	 *            Custom statement the GSP outputs when parsing the SQL string.
	 * @return SQLStatement object that contains information about the supported
	 *         SQL statements.
	 */
	public SQLStatement makeSQLStatement(TCustomSqlStatement tCustomSqlStatement) {
		switch (tCustomSqlStatement.sqlstatementtype) {
		case sstcreatetable:
			return new SQLCreateTableStatement(tCustomSqlStatement);
		case sstdelete:
			return new SQLDeleteStatement(tCustomSqlStatement);
		case sstinsert:
			return new SQLInsertStatement(tCustomSqlStatement);
		case sstselect:
			return new SQLSelectStatement(tCustomSqlStatement);
		case sstupdate:
			return new SQLUpdateStatement(tCustomSqlStatement);
		default:
			return null;
		}
	}
}
