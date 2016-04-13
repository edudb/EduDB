package net.edudb.statement;

import gudusoft.gsqlparser.TCustomSqlStatement;
import gudusoft.gsqlparser.stmt.TCreateTableSqlStatement;

public class SQLCreateTableStatement implements SQLStatement {

	private TCreateTableSqlStatement statement;
	private String columnList;
	private String tableName;
	private String statementString;

	public SQLCreateTableStatement(TCustomSqlStatement tCustomSqlStatement) {
		this.statement = (TCreateTableSqlStatement) tCustomSqlStatement;
		this.columnList = statement.getColumnList().toString();
		this.tableName = statement.getTargetTable().toString();
		this.statementString = statement.toString();
	}

	public String getColumnList() {
		return columnList;
	}

	@Override
	public String getTableName() {
		return tableName;
	}

	@Override
	public SQLStatementType statementType() {
		return SQLStatementType.SQLCreateTableStatement;
	}

	@Override
	public String toString() {
		return statementString;
	}

}
