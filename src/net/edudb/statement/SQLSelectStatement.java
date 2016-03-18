package net.edudb.statement;

import gudusoft.gsqlparser.TCustomSqlStatement;

public class SQLSelectStatement extends SQLStatement {
	
	public SQLSelectStatement(TCustomSqlStatement tCustomSqlStatement) {
		// TODO Auto-generated constructor stub
	}

	@Override
	public SQLStatementType statementType() {
		return SQLStatementType.SQLSelectStatement;
	}

}
