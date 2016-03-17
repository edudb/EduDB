/*
EduDB is made available under the OSI-approved MIT license.

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

package operators;

import DBStructure.DBRecord;
import data_structures.BPlusTree.DBBTreeIterator;

import java.util.ArrayList;

/**
 * Created by mohamed on 5/17/14.
 */
public class ProductIterator implements DBResult, DBIterator {

	ArrayList<DBResult> iterators;
	private boolean finished;
	private SelectColumns columns;
	private ArrayList<DBCond> conditions;

	public ProductIterator() {
		conditions = new ArrayList<>();
		iterators = new ArrayList<>();
		finished = false;
	}

	@Override
	public void print() {
		System.out.println(this);
	}

	@Override
	public int numOfParameters() {
		return 0;
	}

	@Override
	public String toString() {
		return getIterator().toString();
	}

	public void giveIterator(DBResult dbResult) {
		if (dbResult instanceof ProductIterator) {
			dbResult = ((ProductIterator) dbResult).getIterator();
		}
		iterators.add(dbResult);
	}

	// execute join and return resulting iterator
	private DBResult getIterator() {
		DBSimpleiterator iter = new DBSimpleiterator();
		iter.project(columns);
		iter.filter(conditions);
		ArrayList<DBRecord> records = new ArrayList<DBRecord>();
		if (iterators.get(0) instanceof DBBTreeIterator && iterators.get(1) instanceof DBBTreeIterator) {
			DBBTreeIterator itr1 = (DBBTreeIterator) iterators.get(0);
			DBBTreeIterator itr2 = (DBBTreeIterator) iterators.get(1);
			DBRecord element = (DBRecord) itr1.first();
			do {
				DBRecord element2 = (DBRecord) itr2.first();
				do {
					DBRecord record = new DBRecord();
					record.add(element.getValues(), element.getColumns());
					record.add(element2.getValues(), element2.getColumns());
					iter.add(record);
					element2 = (DBRecord) itr2.next();
				} while (element2 != null);
				element = (DBRecord) itr1.next();
			} while (element != null);
			return iter;
		}
		return null;
	}

	public void finish() {
		finished = true;
	}

	@Override
	public void project(SelectColumns columns) {
		if (this.columns == null) {
			this.columns = columns;
		} else {
			this.columns.union(columns);
		}
	}

	@Override
	public void filter(DBCond condition) {
		this.conditions.add(condition);
	}

	@Override
	public Object first() {
		return null;
	}

	@Override
	public Object next() {
		return null;
	}
}
