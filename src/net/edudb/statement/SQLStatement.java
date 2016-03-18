package net.edudb.statement;

public abstract class SQLStatement {

	public abstract SQLStatementType statementType();
	
	public abstract String toString();
	
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
