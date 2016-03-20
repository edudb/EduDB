package net.edudb.statement;

import gudusoft.gsqlparser.TCustomSqlStatement;
import gudusoft.gsqlparser.stmt.TUpdateSqlStatement;

public class SQLUpdateStatement implements SQLStatement {
	
	private TUpdateSqlStatement statement;
	
	public SQLUpdateStatement(TCustomSqlStatement tCustomSqlStatement) {
		this.statement = (TUpdateSqlStatement) tCustomSqlStatement;
	}
	
	public String getTargetTableString() {
		return statement.getTargetTable().toString();
	}
	
	public TUpdateSqlStatement getStatement() {
		return statement;
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
