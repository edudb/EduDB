package net.edudb.statement;

import gudusoft.gsqlparser.TCustomSqlStatement;
import gudusoft.gsqlparser.stmt.TUpdateSqlStatement;

public class SQLUpdateStatement extends SQLStatement {
	
	private TUpdateSqlStatement statement;
	
	public SQLUpdateStatement(TCustomSqlStatement tCustomSqlStatement) {
		this.statement = (TUpdateSqlStatement) tCustomSqlStatement;
	}

	@Override
	public SQLStatementType statementType() {
		return SQLStatementType.SQLUpdateStatement;
	}

	@Override
	public String toString() {
		return statement.toString();
	}

}
