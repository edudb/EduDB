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
	
	public String getTargetTableString() {
		return statement.getTargetTable().toString();
	}
	
	public ArrayList<String> getColumnList() {
		ArrayList<String> columns = new ArrayList<String>();
		for (int i = 0; i < statement.getColumnList().size(); i++) {
			columns.add(statement.getColumnList().getObjectName(i).toString());
		}
		
		return columns;
	}
	
	public ArrayList<String> getValueList() {
		
		ArrayList<String> values = new ArrayList<String>();
		TResultColumnList columnList = statement.getValues().getMultiTarget(0).getColumnList();
		for (int i = 0; i < columnList.size(); i++) {
			values.add(columnList.getResultColumn(i).toString());
		}
		
		return values;
		
//		System.out.println("VALS:" + statement.getValues().getMultiTarget(0));
//		System.out.println("COLSSS: " + statement.getColumnList());
//		
//		return statement.getValues().getMultiTarget(0).getColumnList();
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
