/*
EduDB is made available under the OSI-approved MIT license.

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

package net.edudb.operator;

import java.util.ArrayList;
import java.util.ListIterator;

import net.edudb.server.ServerWriter;
import net.edudb.structure.Record;

public class DBSimpleiterator implements DBResult, ListIterator, DBIterator {

	private ArrayList<Record> records;
	private SelectColumns columns;
	private ArrayList<DBCond> conditions;

	public DBSimpleiterator() {
		this.records = new ArrayList<Record>();
	}

	@Override
	public String toString() {
		String out = "";
		for (int i = 0; i < records.size(); i++) {
			if (!records.get(i).evaluate(conditions).equals("")) {
				out += records.get(i).project(columns) + "\n";
			}
		}
		return out;
	}

	@Override
	public void print() {
		ServerWriter.getInstance().writeln(this);
	}

	@Override
	public int numOfParameters() {
		return 0;
	}

	@Override
	public boolean hasNext() {
		return false;
	}

	@Override
	public Object next() {
		return null;
	}

	@Override
	public boolean hasPrevious() {
		return false;
	}

	@Override
	public Object previous() {
		return null;
	}

	@Override
	public int nextIndex() {
		return 0;
	}

	@Override
	public int previousIndex() {
		return 0;
	}

	@Override
	public void remove() {

	}

	@Override
	public void set(Object o) {

	}

	@Override
	public void add(Object o) {
		records.add((Record) o);
	}

	@Override
	public void project(SelectColumns columns) {
		if (this.columns == null) {
			this.columns = columns;
		} else {
			this.columns.union(columns);
		}
	}

	public void filter(ArrayList<DBCond> conditions) {
		this.conditions = conditions;
	}

	public void filter(DBCond condition) {

	}

	@Override
	public Object first() {
		return null;
	}
}
