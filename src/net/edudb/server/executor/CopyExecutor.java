package net.edudb.server.executor;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.regex.Matcher;
import net.edudb.console.executor.ConsoleExecutorChain;
import net.edudb.data_type.DataType;
import net.edudb.data_type.DataTypeFactory;
import net.edudb.engine.Utility;
import net.edudb.server.ServerWriter;
import net.edudb.statistics.Schema;
import net.edudb.structure.Column;
import net.edudb.structure.Record;
import net.edudb.structure.TableRecord;
import net.edudb.structure.table.Table;
import net.edudb.structure.table.TableManager;
/**
 * Handles the copy command.
 * 
 * @author Ahmed Abdul Badie
 *
 */
public class CopyExecutor implements ConsoleExecutorChain {
	private ConsoleExecutorChain nextElement;
	/**
	 * Matches strings of the form: <br>
	 * <br>
	 * <b>COPY table_name DELIMITER 'delimiter';</b><br>
	 * <br>
	 * and captures <b>table_name</b> and <b>delimiter</b> in the matcher's
	 * groups one and two, respectively.
	 */
	private String regex = "^(?:(?i)copy)\\s+(\\D\\w*)\\s+(?:(?i)delimiter)\\s+\\'(.+)\\'\\s*\\;?$";

	@Override
	public void setNextElementInChain(ConsoleExecutorChain chainElement) {
		this.nextElement = chainElement;
	}

	@Override
	public void execute(String string) {
		/**
		 * The string is passed as a command in the first line and data,
		 * separated by a delimited, in subsequent lines.
		 */
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
				DataTypeFactory typeFactory = new DataTypeFactory();

				int count = 0;
				for (int i = 1; i < values.length; i++) {
					String[] row = values[i].split(matcher.group(2));
					LinkedHashMap<Column, DataType> data = new LinkedHashMap<>();
					int size = row.length;
					for (int j = 0; j < size; j++) {
						Column column = columns.get(j);
						data.put(column, typeFactory.makeType(column.getTypeName(), row[j]));
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
