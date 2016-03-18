package net.edudb.statement;

import gudusoft.gsqlparser.TCustomSqlStatement;
import gudusoft.gsqlparser.stmt.TCreateTableSqlStatement;

public class SQLCreateTableStatement extends SQLStatement {
	
	private TCreateTableSqlStatement statement;

	public SQLCreateTableStatement(TCustomSqlStatement tCustomSqlStatement) {
		this.statement = (TCreateTableSqlStatement) tCustomSqlStatement;
	}
	
	public String getTableName() {
		return statement.getTableName().toString();
	}
	
	public String getColumnListString() {
		return statement.getColumnList().toString();
	}
	
	@Override
	public SQLStatementType statementType() {
		return SQLStatementType.SQLCreateTableStatement;
	}

	@Override
	public String toString() {
		return statement.toString();
	}

}
