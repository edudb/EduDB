package net.edudb.statement;

import gudusoft.gsqlparser.TCustomSqlStatement;

/**
 * 
 * @author ahmedabadie
 *
 */
public class SQLStatementFactory {

	/**
	 * 
	 * @param tCustomSqlStatement
	 *            Custom statement the GSP outputs when parsing the SQL string.
	 * @return SQLStatement Object that is responsible for the supported SQL
	 *         statements.
	 */
	public SQLStatement getSQLStatement(TCustomSqlStatement tCustomSqlStatement) {
		switch (tCustomSqlStatement.sqlstatementtype) {
		case sstcreatetable:
			return new SQLCreateTableStatement(tCustomSqlStatement);
		case sstselect:
			return new SQLSelectStatement(tCustomSqlStatement);
		case sstinsert:
			return new SQLInsertStatement(tCustomSqlStatement);
		case sstupdate:
			return new SQLUpdateStatement(tCustomSqlStatement);
		default:
			return null;
		}
	}
}
