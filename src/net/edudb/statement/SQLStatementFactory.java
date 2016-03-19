package net.edudb.statement;

import gudusoft.gsqlparser.TCustomSqlStatement;

public class SQLStatementFactory {
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
