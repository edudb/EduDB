package net.edudb.server.executor;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.regex.Matcher;
import net.edudb.console.executor.ConsoleExecutorChain;
import net.edudb.data_type.DataType;
import net.edudb.data_type.IntegerType;
import net.edudb.engine.Utility;
import net.edudb.server.ServerWriter;
import net.edudb.statistics.Schema;
import net.edudb.structure.Column;
import net.edudb.structure.Record;
import net.edudb.structure.TableRecord;
import net.edudb.structure.table.Table;
import net.edudb.structure.table.TableManager;

public class CopyExecutor implements ConsoleExecutorChain {
	private ConsoleExecutorChain nextElement;
	private String regex = "^(?:(?i)copy)\\s+(\\D\\w*)\\s+(?:(?i)delimiter)\\s+\\'(.+)\\'\\s*\\;?$";

	@Override
	public void setNextInChain(ConsoleExecutorChain chainElement) {
		this.nextElement = chainElement;
	}

	@Override
	public void execute(String string) {
		if (string.toLowerCase().startsWith("copy")) {
			String[] values = string.split("\r\n");
			Matcher matcher = Utility.getMatcher(values[0], regex);
			if (matcher.matches()) {
				String tableName = matcher.group(1);
				if (!Schema.getInstance().chekTableExists(tableName)) {
					ServerWriter.getInstance().writeln("Table '" + tableName + "' is not available.");
					return;
				}
				Table table = TableManager.getInstance().read(tableName);
				ArrayList<Column> columns = Schema.getInstance().getColumns(tableName);

				int count = 0;
				for (int i = 1; i < values.length; i++) {
					String[] row = values[i].split(matcher.group(2));
					LinkedHashMap<Column, DataType> data = new LinkedHashMap<>();
					int size = row.length;
					for (int j = 0; j < size; j++) {
						data.put(columns.get(j), new IntegerType(Integer.parseInt(row[j])));
					}

					Record record = new TableRecord(data);
					table.addRecord(record);
					++count;
				}
				
				ServerWriter.getInstance().writeln("Copied '" + count + "' records");
				return;
			}
		}
		nextElement.execute(string);
	}

}
