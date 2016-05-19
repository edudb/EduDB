package net.edudb.operator.executor;

import java.util.LinkedHashMap;

import net.edudb.data_type.DataType;
import net.edudb.engine.Config;
import net.edudb.operator.CreateTableOperator;
import net.edudb.operator.Operator;
import net.edudb.relation.Relation;
import net.edudb.relation.VolatileRelation;
import net.edudb.server.ServerWriter;
import net.edudb.statement.SQLCreateTableStatement;
import net.edudb.statistics.Schema;
import net.edudb.structure.table.Table;
import net.edudb.structure.table.TableFactory;
import net.edudb.structure.table.TableManager;

/**
 * Executes the SQL CREATE TABLE statement.
 * 
 * @author Ahmed Abdul Badie
 *
 */
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
			boolean executable = true;

			String[] columnNames = statement.getColumnNames();
			String[] dataTypeNames = statement.getDataTypeNames();
			LinkedHashMap<String, String> columnTypes = new LinkedHashMap<>();
			for (int i = 0; i < columnNames.length; i++) {
				if (DataType.isSupported(dataTypeNames[i])) {
					columnTypes.put(columnNames[i], dataTypeNames[i]);
				} else {
					ServerWriter.getInstance().writeln("Type '" + dataTypeNames[i] + "' is not a supported type");
					executable = false;
				}
			}

			if (!executable) {
				return null;
			}

			String line = statement.getTableName();
			line += " " + statement.getColumnList();
			Schema.getInstance().addTable(line);

			TableFactory tableFactory = new TableFactory();
			Table table = tableFactory.makeTable(Config.tableType(), statement.getTableName());
			table.setColumnTypes(columnTypes);

			TableManager.getInstance().write(table);

			ServerWriter.getInstance().writeln("Created table '" + table.getName() + "'");

			Relation relation = new VolatileRelation(table);

			return relation;
		}
		return nextElement.execute(operator);
	}

}
