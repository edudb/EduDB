/*
EduDB is made available under the OSI-approved MIT license.

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

package net.edudb.structure.table;

import java.io.File;
import java.util.HashMap;
import net.edudb.engine.Config;
import net.edudb.engine.FileManager;
import net.edudb.statistics.Schema;

/**
 * 
 * TableManager is a singleton that is a wrapper class around
 * TableAbstractFactory. It handles the reading and writing of tables by
 * contacting the File Manager.
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

	/**
	 * Reads a table from the disk if it is not available in the table buffer
	 * pool.
	 * 
	 * @param tableName
	 *            Name of the table to read.
	 * @return The read table.
	 */
	public synchronized Table read(String tableName) {
		Table table = null;

		table = tableBuffer.get(tableName);

		if (table != null) {
			return table;
		}

		table = FileManager.getInstance().readTable(tableName);

		// TableAbstractFactory tableFactory = new TableReaderFactory();
		// TableReader tableReader = tableFactory.getReader(Config.tableType());
		//
		// try {
		// table = tableReader.read(tableName);
		tableBuffer.put(tableName, table);
		// } catch (ClassNotFoundException | IOException e) {
		// e.printStackTrace();
		// }
		return table;
	}

	/**
	 * Adds the table to the buffer pool and writes it to disk.
	 * 
	 * @param table
	 *            The table to write.
	 */
	public synchronized void write(Table table) {
		// TableAbstractFactory tableFactory = new TableWriterFactory();
		// TableWriter tableWriter = tableFactory.getWriter(Config.tableType());
		//
		// try {
		tableBuffer.put(table.getName(), table);
		FileManager.getInstance().writeTable(table);
		// tableWriter.write(table);
		// } catch (IOException e) {
		// e.printStackTrace();
		// }
	}

	/**
	 * Writes all the tables to disk. Used when closing the database for the
	 * data to be persisted.
	 */
	public void writeAll() {
		for (Table table : tableBuffer.values()) {
			this.write(table);
		}
	}

	/**
	 * Deletes the table by deleting its pages from disk, removing the table
	 * from the schema file, and removing the table from disk.
	 * 
	 * @param table
	 *            The table to delete.
	 */
	public void delete(Table table) {
		table.deletePages();

		String path = Config.tablesPath() + table.getName() + ".table";
		File file = new File(path);

		if (file.exists()) {
			file.delete();
		}

		Schema.getInstance().removeTable(table.getName());

		tableBuffer.remove(table.getName());
	}
}
