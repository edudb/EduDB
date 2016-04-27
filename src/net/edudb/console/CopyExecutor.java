/*
EduDB is made available under the OSI-approved MIT license.

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

package net.edudb.console;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.regex.Matcher;
import net.edudb.data_type.DataType;
import net.edudb.data_type.IntegerType;
import net.edudb.relational_algebra.Translator;
import net.edudb.server.ServerWriter;
import net.edudb.statistics.Schema;
import net.edudb.structure.Column;
import net.edudb.structure.Record;
import net.edudb.structure.TableRecord;
import net.edudb.structure.table.Table;
import net.edudb.structure.table.TableManager;

/**
 * 
 * @author Ahmed Abdul Badie
 *
 */
public class CopyExecutor implements ConsoleExecutorChain {
	private ConsoleExecutorChain nextElement;
	String regex = "\\A(?:(?i)copy)\\s+(\\w+)\\s+(?:(?i)from)\\s+\\'(.+)\\'\\s+(?:(?i)delimiter)\\s+\\'(.+)\\'\\s*\\;?\\z";

	@Override
	public void setNextInChain(ConsoleExecutorChain chainElement) {
		this.nextElement = chainElement;
	}

	@Override
	public void execute(String string) {
		if (string.toLowerCase().startsWith("copy")) {
			Matcher matcher = Translator.getMatcher(string, regex);
			if (matcher.matches()) {
				String tableName = matcher.group(1);
				if (!Schema.getInstance().chekTableExists(tableName)) {
					ServerWriter.getInstance().writeln("Table '" + tableName + "' is not available.");
					return;
				}
				Table table = TableManager.getInstance().read(tableName);
				ArrayList<Column> columns = Schema.getInstance().getColumns(tableName);

				String path = matcher.group(2);
				int count = 0;
				try {
					List<String> lines = Files.readAllLines(Paths.get(path));
					for (int i = 0; i < lines.size(); i++) {
						String[] values = lines.get(i).split(matcher.group(3));
						LinkedHashMap<Column, DataType> data = new LinkedHashMap<>();
						int size = values.length;
						for (int j = 0; j < size; j++) {
							data.put(columns.get(j), new IntegerType(Integer.parseInt(values[j])));
						}

						Record record = new TableRecord(data);
						table.addRecord(record);
						++count;
					}
					ServerWriter.getInstance().writeln("Copied '" + count + "' records");
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else {
				ServerWriter.getInstance().writeln("Unknown command 'copy'");
			}
			ServerWriter.getInstance().writeln("[edudb::endofstring]");
			return;
		}
		nextElement.execute(string);
	}

}
