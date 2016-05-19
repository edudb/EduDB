/*
EduDB is made available under the OSI-approved MIT license.

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

package net.edudb.console.executor;

import java.util.regex.Matcher;
import net.edudb.engine.Utility;
import net.edudb.server.ServerWriter;
import net.edudb.statistics.Schema;
import net.edudb.structure.table.Table;
import net.edudb.structure.table.TableManager;

/**
 * Drops a table from a database.
 * 
 * @author Ahmed Abdul Badie
 *
 */
public class DropTableExecutor implements ConsoleExecutorChain {
	private ConsoleExecutorChain nextElement;
	/**
	 * Matches strings of the form: <br>
	 * <br>
	 * <b>DROP TABLE table_name;</b><br>
	 * <br>
	 * and captures the <b>table_name</b> in the matchers group one.
	 */
	private String regex = "\\A(?:(?i)drop)\\s+(?:(?i)table)\\s+(\\D\\w*)\\s*;?\\z";

	@Override
	public void setNextElementInChain(ConsoleExecutorChain chainElement) {
		this.nextElement = chainElement;
	}

	@Override
	public void execute(String string) {
		if (string.toLowerCase().startsWith("drop")) {
			Matcher matcher = Utility.getMatcher(string, regex);
			if (matcher.matches()) {
				String tableName = matcher.group(1);
				if (!Schema.getInstance().chekTableExists(tableName)) {
					ServerWriter.getInstance().writeln("Table '" + tableName + "' does not exist");
					return;
				}

				Table table = TableManager.getInstance().read(tableName);
				TableManager.getInstance().delete(table);

				ServerWriter.getInstance().writeln("Dropped table '" + tableName + "'");

				return;
			}
		}
		nextElement.execute(string);
	}

}
