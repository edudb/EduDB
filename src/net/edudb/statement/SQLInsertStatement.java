package net.edudb.statement;

import gudusoft.gsqlparser.TCustomSqlStatement;

public class SQLInsertStatement extends SQLStatement {
	
	public SQLInsertStatement(TCustomSqlStatement tCustomSqlStatement) {
		// TODO Auto-generated constructor stub
	}

	@Override
	public SQLStatementType statementType() {
		return SQLStatementType.SQLInsertStatement;
	}

}
