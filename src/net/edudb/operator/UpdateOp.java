/*
EduDB is made available under the OSI-approved MIT license.

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

package net.edudb.operator;

import net.edudb.index.BPlusTree.DBBTreeIterator;
import net.edudb.operator.FilterOperator;
import net.edudb.operator.Operator;
import net.edudb.structure.Record;
import net.edudb.transcation.Page;
import net.edudb.transcation.PageRead;
import net.edudb.transcation.PageWrite;
import net.edudb.transcation.Step;

import java.util.ArrayList;

public class UpdateOp implements Operator {

	private String tableName;
	private ArrayList<DBAssignment> assignments;
	private DBCond condition;
	private Page page;
	private int state;

	public UpdateOp(String tableName, ArrayList<DBAssignment> assignments, DBCondition condition) {
		this.tableName = tableName;
		this.assignments = assignments;
		this.condition = condition;
	}

	@Override
	public DBResult execute() {
		FilterOperator filterOperator = new FilterOperator();
		RelationOperator relationOperator = new RelationOperator();
		relationOperator.setTableName(tableName);
		filterOperator.giveParameter(relationOperator);
		filterOperator.giveParameter(condition);
		DBBTreeIterator resultIterator = (DBBTreeIterator) filterOperator.execute();
		Record record = (Record) resultIterator.first();
		do {
			record.update(assignments);
			record = (Record) resultIterator.next();
		} while (record != null);
		resultIterator.write();
		return resultIterator;
	}

	@Override
	public DBParameter[] getChildren() {
		return new DBParameter[0];
	}

	@Override
	public void giveParameter(DBParameter par) {

	}

	@Override
	public void print() {

	}

	@Override
	public int numOfParameters() {
		return 0;
	}

	public ArrayList<Step> getSteps() {
		ArrayList<Step> out = new ArrayList<>();
		PageRead read = new PageRead(this, tableName, true);
		out.add(read);

		PageWrite write = new PageWrite(this);

		out.add(write);
		return out;
	}

	public void runStep(Page page) {
		this.page = page;
		System.out.println("run");
		System.out.println(page);
		FilterOperator filterOperator = new FilterOperator();
		filterOperator.giveParameter(page.getData());
		filterOperator.giveParameter(condition);
		DBIterator iterator = (DBIterator) filterOperator.execute();
		Record record = (Record) iterator.first();
		do {
			record.update(assignments);
			record = (Record) iterator.next();
		} while (record != null);
	}

	public Page getPage() {
		return page;
	}
}
