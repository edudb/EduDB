/*
EduDB is made available under the OSI-approved MIT license.

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

package net.edudb.structure;

import java.util.ArrayList;

import net.edudb.index.BPlusTree.BTree;
import net.edudb.index.BPlusTree.DBBTree;
import net.edudb.index.BPlusTree.DBBTreeIterator;
import net.edudb.operator.DBResult;
import net.edudb.operator.SelectResult;

public class DBTable {
	// TODO initialize object
	/**
	 * @uml.property name="tableName"
	 * @uml.associationEnd qualifier="key:java.lang.String DBStructure.DBTable"
	 */
	private String tableName;
	/**
	 * @uml.property name="indices"
	 * @uml.associationEnd multiplicity="(0 -1)"
	 *                     elementType="data_structures.BPlusTree.DBBTree"
	 */
	private ArrayList<DBIndex> indices;

	public DBTable(String tableName) {
		this.tableName = tableName;
		this.indices = new ArrayList<DBIndex>();
		// TODO add other indices
		DBBTree btree = new DBBTree(tableName);
		btree.readTable();
		indices.add((DBIndex) btree);
	}

	/**
	 * @return
	 * @uml.property name="tableName"
	 */
	public String getTableName() {
		return tableName;
	}

	public DBIndex getPrimaryIndex() {
		return indices.get(0);
		// TODO primary is at index 0
	}

	public DBResult getData() {
		DBBTree primary = (DBBTree) indices.get(0);
		return new DBBTreeIterator(primary);
	}
}
