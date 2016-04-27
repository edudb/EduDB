/*
EduDB is made available under the OSI-approved MIT license.

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

package net.edudb.structure.table;

import java.io.IOException;
import java.util.HashMap;
import net.edudb.engine.Config;

/**
 * 
 * TableManager is a wrapper class around TableAbstractFactory. It handles the
 * reading and writing of tables using the default file type defined in
 * net.edudb.engine.Config.
 * 
 * @author Ahmed Abdul Badie
 *
 */
public class TableManager {

	private static TableManager instance = new TableManager();
	private HashMap<String, Table> tableBuffer;

	private TableManager() {
		this.tableBuffer = new HashMap<>();
	}

	public static TableManager getInstance() {
		return instance;
	}

	public synchronized Table read(String tableName) {
		Table table = null;

		table = tableBuffer.get(tableName);

		if (table != null) {
			return table;
		}

		TableAbstractFactory tableFactory = new TableReaderFactory();
		TableReader tableReader = tableFactory.getReader(Config.tableType());

		try {
			table = tableReader.read(tableName);
			tableBuffer.put(tableName, table);
		} catch (ClassNotFoundException | IOException e) {
			e.printStackTrace();
		}
		return table;
	}

	public synchronized void write(Table table) {
		TableAbstractFactory tableFactory = new TableWriterFactory();
		TableWriter tableWriter = tableFactory.getWriter(Config.tableType());

		try {
			tableBuffer.put(table.getName(), table);
			tableWriter.write(table);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void writeAll() {
		for (Table table : tableBuffer.values()) {
			this.write(table);
		}
	}
}
