package net.edudb.operator.executor;

import net.edudb.engine.Config;
import net.edudb.operator.CreateTableOperator;
import net.edudb.operator.Operator;
import net.edudb.relation.Relation;
import net.edudb.relation.VolatileRelation;
import net.edudb.statement.SQLCreateTableStatement;
import net.edudb.statistics.Schema;
import net.edudb.structure.table.Table;
import net.edudb.structure.table.TableFactory;
import net.edudb.structure.table.TableManager;

public class CreateTableExecutor extends PostOrderOperatorExecutor implements OperatorExecutionChain {
	private OperatorExecutionChain nextElement;

	@Override
	public void setNextElementInChain(OperatorExecutionChain chainElement) {
		this.nextElement = chainElement;
	}

	@Override
	public Relation execute(Operator operator) {
		if (operator instanceof CreateTableOperator) {
			SQLCreateTableStatement statement = (SQLCreateTableStatement) operator.getParameter();

			String line = statement.getTableName();
			line += " " + statement.getColumnList();
			Schema.getInstance().addTable(line);

			TableFactory tableFactory = new TableFactory();
			Table table = tableFactory.makeTable(Config.tableType(), statement.getTableName());
			TableManager.getInstance().write(table);
			
			Relation relation = new VolatileRelation(table);

			return relation;
		}
		return nextElement.execute(operator);
	}

}
