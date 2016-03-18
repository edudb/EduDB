package net.edudb.statement;

import gudusoft.gsqlparser.TCustomSqlStatement;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;

public class SQLSelectStatement implements SQLStatement {
	
	private TSelectSqlStatement statement;
	
	public SQLSelectStatement(TCustomSqlStatement tCustomSqlStatement) {
		this.statement = (TSelectSqlStatement) tCustomSqlStatement;
	}

	@Override
	public SQLStatementType statementType() {
		return SQLStatementType.SQLSelectStatement;
	}

	@Override
	public String toString() {
		return statement.toString();
	}

}
