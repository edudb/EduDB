/*
EduDB is made available under the OSI-approved MIT license.

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

package net.edudb.structure;

import java.io.Serializable;
import java.util.LinkedHashMap;

import net.edudb.data_type.DataType;
import net.edudb.data_type.IntegerType;
import net.edudb.ebtree.EBTree;
import net.edudb.expression.*;

public class TableRecord implements Record, Serializable {

	private static final long serialVersionUID = -3305225200308977932L;

	LinkedHashMap<DBColumn, DataType> data;

	public TableRecord() {
		this.data = new LinkedHashMap<>();
	}

	public TableRecord(LinkedHashMap<DBColumn, DataType> data) {
		this.data = data;
	}

	public void addValue(DBColumn key, DataType value) {
		data.put(key, value);
	}

	public boolean evaluate(BinaryExpressionTree expressionTree) {
		return expressionTree.evaluate(data);
	}

	@Override
	public String toString() {
		return data.toString();
	}

	public static void main(String[] args) {
		TableRecord r = new TableRecord();
		DBColumn a = new DBColumn(1, "a", "test");
		DBColumn b = new DBColumn(2, "b", "test");
		DBColumn c = new DBColumn(3, "c", "test");
		r.addValue(a, new IntegerType(10));
		r.addValue(b, new IntegerType(11));
		r.addValue(c, new IntegerType(20));

		BinaryExpressionNode and = new ANDLogicalOperator();
		and.setLeftChild(new Expression(a, new IntegerType(10), OperatorType.Equal));
		and.setRightChild(new Expression(b, new IntegerType(1), OperatorType.Equal));

		BinaryExpressionNode or = new ANDLogicalOperator();
		or.setRightChild(new Expression(c, new IntegerType(20), OperatorType.Equal));

		EBTree et = new BinaryExpressionTree(and);
		et.addNode(or);

		System.out.println(r.evaluate((BinaryExpressionTree) et));
	}

}
