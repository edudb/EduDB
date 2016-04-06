package net.edudb.statement;

import java.util.ArrayList;

import gudusoft.gsqlparser.TCustomSqlStatement;
import gudusoft.gsqlparser.nodes.TResultColumnList;
import gudusoft.gsqlparser.stmt.TInsertSqlStatement;

public class SQLInsertStatement implements SQLStatement {

	/**
	 * ATTENTION
	 * 
	 * Do not access `statement` from concurrent threads as it will cause
	 * exceptions.
	 */
	private volatile TInsertSqlStatement statement;
	private String statementString;
	private String tableName;
	private ArrayList<String> columnList;
	private ArrayList<String> valueList;

	public SQLInsertStatement(TCustomSqlStatement tCustomSqlStatement) {
		this.statement = (TInsertSqlStatement) tCustomSqlStatement;
		this.statementString = statement.toString();
		this.tableName = statement.getTargetTable().toString();
		this.columnList = this.extractColumnList();
		this.valueList = this.extractValueList();
	}

	public String getTargetTableName() {
		return tableName;
	}

	private ArrayList<String> extractColumnList() {
		if (statement.getColumnList() == null) {
			return null;
		}
		ArrayList<String> columns = new ArrayList<>();
		for (int i = 0; i < statement.getColumnList().size(); i++) {
			columns.add(statement.getColumnList().getObjectName(i).toString());
		}

		return columns;
	}

	public ArrayList<String> getColumnList() {
		return columnList;
	}

	private ArrayList<String> extractValueList() {
		ArrayList<String> values = new ArrayList<>();
		TResultColumnList columnList = statement.getValues().getMultiTarget(0).getColumnList();
		for (int i = 0; i < columnList.size(); i++) {
			values.add(columnList.getResultColumn(i).toString());
		}

		return values;
	}

	public ArrayList<String> getValueList() {
		return valueList;
	}

	@Override
	public SQLStatementType statementType() {
		return SQLStatementType.SQLInsertStatement;
	}

	@Override
	public String toString() {
		return statementString;
	}

}
