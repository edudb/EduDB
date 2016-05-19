/*
EduDB is made available under the OSI-approved MIT license.

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

package net.edudb.statistics;

import net.edudb.engine.FileManager;
import net.edudb.structure.Column;
import net.edudb.structure.table.Table;
import net.edudb.structure.table.TableManager;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Set;

import com.google.common.collect.Iterables;

/**
 * Created by mohamed on 4/1/14.
 */
public class Schema {

	private static Schema instance = new Schema();
	private HashMap<String, ArrayList<String>> schema;

	private Schema() {
		schema = new HashMap<>();
		setSchema();
	}

	public static Schema getInstance() {
		return instance;
	}

	/**
	 * Checks the availability of the given table in the database's schema.
	 * 
	 * @param tableName
	 *            The name of the table to check.
	 * @return The availability of the table.
	 */
	public boolean chekTableExists(String tableName) {
		return schema.get(tableName) != null;
	}

	private void setSchema() {
		ArrayList<String> lines = FileManager.readFile(FileManager.getSchema());
		for (String line : lines) {
			putTable(line);
		}
	}

	/**
	 * Returns the columns of the required table.
	 * 
	 * @param tableName
	 *            The name of the required table.
	 * @return Columns of the required table as an {@link ArrayList}.
	 */
	public ArrayList<Column> getColumns(String tableName) {
		ArrayList<Column> columns = new ArrayList<>();
		Table table = TableManager.getInstance().read(tableName);
		LinkedHashMap<String, String> columnTypes = table.getColumnTypes();
		for (int i = 1; i <= columnTypes.size(); i++) {
			String columnName = Iterables.get(columnTypes.keySet(), i - 1);
			String columnType = Iterables.get(columnTypes.values(), i - 1);
			columns.add(new Column(i, columnName, tableName, columnType));
		}

		return columns;
	}

	// add table to schema object
	private void putTable(String line) {
		String[] tokens = line.split(" ");
		String TableName = tokens[0];
		ArrayList<String> columns = new ArrayList<String>();
		for (int i = 1; i < tokens.length; i += 2) {
			String columnName = tokens[i];
			columns.add(columnName);
		}
		schema.put(TableName, columns);
	}

	// add table to schema file
	public void addTable(String line) {
		putTable(line);
		line += System.lineSeparator();
		FileManager.addToFile(FileManager.getSchema(), line);
	}

	public void removeTable(String tableName) {

		ArrayList<String> schema = FileManager.readFile(FileManager.getSchema());

		String schemaData = "";

		for (String string : schema) {
			if (!string.startsWith(tableName)) {
				schemaData += string + "\r\n";
			}
		}

		File file = new File(FileManager.getSchema());
		if (file.exists()) {
			file.delete();
		}

		this.schema.remove(tableName);

		FileManager.writeToFile(schemaData, FileManager.getSchema());
	}

	public HashMap<String, ArrayList<String>> getSchema() {
		return schema;
	}

	public int getCount(String tableName) {
		return schema.get(tableName).size();
	}

	public ArrayList<String> getColumnNames(String tableName) {
		ArrayList<String> columnNames = new ArrayList<>();
		int count = schema.get(tableName).size();
		for (int i = 0; i < count; i++) {
			columnNames.add(schema.get(tableName).get(i));
		}
		return columnNames;
	}

	public int getColumnNumber(String name, String tableName) {
		return getColumnNames(tableName).indexOf(name);
	}

	public Set<String> getTableNames() {
		return schema.keySet();
	}
}
