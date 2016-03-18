package net.edudb.statement;

import gudusoft.gsqlparser.TCustomSqlStatement;

public abstract class SQLStatement {
	
//	protected TCustomSqlStatement tCustomSqlStatement;
//	
//	public SQLStatement(TCustomSqlStatement tCustomSqlStatement); {
//		this.tCustomSqlStatement = tCustomSqlStatement;
//	}

	public abstract SQLStatementType statementType();
//	{
//		switch (tCustomSqlStatement.sqlstatementtype) {
//		case sstcreatetable:
//			return SQLStatementType.SQLCreateTableStatement;
//		case sstselect:
//			return SQLStatementType.SQLSelectStatement;
//		case sstinsert:
//			return SQLStatementType.SQLInsertStatement;
//		case sstupdate:
//			return SQLStatementType.SQLUpdateStatement;
//		default:
//			return SQLStatementType.SQLNoStatement;
//		}
//	}
}
