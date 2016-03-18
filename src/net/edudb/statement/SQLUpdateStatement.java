package net.edudb.statement;

import gudusoft.gsqlparser.TCustomSqlStatement;

public class SQLUpdateStatement extends SQLStatement {
	
	public SQLUpdateStatement(TCustomSqlStatement tCustomSqlStatement) {
		// TODO Auto-generated constructor stub
	}

	@Override
	public SQLStatementType statementType() {
		return SQLStatementType.SQLUpdateStatement;
	}

}
