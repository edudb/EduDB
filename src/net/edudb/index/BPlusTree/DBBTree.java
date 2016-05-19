/*
EduDB is made available under the OSI-approved MIT license.

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

package net.edudb.index.BPlusTree;

import net.edudb.data_type.IntegerType;
import net.edudb.db_operator.DBIterator;
import net.edudb.engine.FileManager;
import net.edudb.server.ServerWriter;
import net.edudb.statistics.Schema;
import net.edudb.structure.Column;
import net.edudb.structure.DBIndex;
import net.edudb.structure.DBRecord;
import java.util.ArrayList;

/**
 * Created by mohamed on 4/11/14.
 */
public class DBBTree extends BTree<Integer, DBRecord> implements DBIndex {
	/**
	 * @uml.property name="tableName"
	 * @uml.associationEnd
	 */
	private String tableName;
	/**
	 * @uml.property name="columnNames"
	 */
	private ArrayList<Column> columnNames;

	public DBBTree(String tableName) {
		this.tableName = tableName;
		columnNames = Schema.getInstance().getColumns(tableName);
	}

	public void insert(int key, DBRecord value) {
		super.insert(key, value);
	}

	@Override
	public DBIterator getIterator() {
		return new DBBTreeIterator(this);
	}

	@Override
	public DBIndex getCopy() {
		DBBTree out = new DBBTree(tableName);
		DBIterator iter = getIterator();
		DBRecord record = (DBRecord) iter.first();
		do {
			out.insert(((IntegerType) record.getValue(0)).getInteger(), record.getCopy());
			record = (DBRecord) iter.next();
		} while (record != null);
		return out;
	}

	public void remove(int key) {
		super.delete(key);
	}

	public void readTable() {
		String table = FileManager.getTable(tableName);
		ArrayList<String> lines = FileManager.readFile(table);
		for (int i = 0; i < lines.size(); i++) {
			String[] line = lines.get(i).split(",");
			Integer key = Integer.parseInt(line[0]);
			DBRecord record = new DBRecord(line, tableName);
			this.insert(key, record);
		}
	}

	public ArrayList<Column> getColumns() {
		return columnNames;
	}

	public void write() {
		String table = FileManager.getTable(tableName);
		String data = commit();
		FileManager.writeToFile(data, table);
	}

	public static void main(String[] args) {
		DBBTree tree = new DBBTree("persons");
		tree.readTable();
		DBBTree copy = (DBBTree) tree.getCopy();
		ServerWriter.getInstance().writeln("tree");
		tree.print();
		ServerWriter.getInstance().writeln("copy");
		copy.print();
		((DBRecord) copy.getSmallest().getValue(0)).setValue(0, new IntegerType(5));
		ServerWriter.getInstance().writeln("tree");
		tree.print();
		ServerWriter.getInstance().writeln("copy");
		copy.print();
	}
}
