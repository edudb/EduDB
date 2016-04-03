package net.edudb.statement;

import java.util.ArrayList;

import gudusoft.gsqlparser.TCustomSqlStatement;
import gudusoft.gsqlparser.nodes.TResultColumnList;
import gudusoft.gsqlparser.stmt.TInsertSqlStatement;

public class SQLInsertStatement implements SQLStatement {
	
	private TInsertSqlStatement statement;
	
	public SQLInsertStatement(TCustomSqlStatement tCustomSqlStatement) {
		this.statement = (TInsertSqlStatement) tCustomSqlStatement;
	}
	
	public String getTargetTableName() {
		return statement.getTargetTable().toString();
	}
	
	public ArrayList<String> getColumnList() {
		ArrayList<String> columns = new ArrayList<>();
		for (int i = 0; i < statement.getColumnList().size(); i++) {
			columns.add(statement.getColumnList().getObjectName(i).toString());
		}
		
		return columns;
	}
	
	public ArrayList<String> getValueList() {
		
		ArrayList<String> values = new ArrayList<>();
		TResultColumnList columnList = statement.getValues().getMultiTarget(0).getColumnList();
		for (int i = 0; i < columnList.size(); i++) {
			values.add(columnList.getResultColumn(i).toString());
		}
		
		return values;
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
