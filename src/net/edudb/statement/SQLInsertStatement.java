package net.edudb.statement;

import gudusoft.gsqlparser.TCustomSqlStatement;
import gudusoft.gsqlparser.stmt.TInsertSqlStatement;

public class SQLInsertStatement implements SQLStatement {
	
	private TInsertSqlStatement statement;
	
	public SQLInsertStatement(TCustomSqlStatement tCustomSqlStatement) {
		this.statement = (TInsertSqlStatement) tCustomSqlStatement;
	}
	
	public String getTargetTableString() {
		return statement.getTargetTable().toString();
	}

	@Override
	public SQLStatementType statementType() {
		return SQLStatementType.SQLInsertStatement;
	}

	@Override
	public String toString() {
		return statement.toString();
	}

}
