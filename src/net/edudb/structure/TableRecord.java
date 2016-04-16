/*
EduDB is made available under the OSI-approved MIT license.

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

package net.edudb.structure;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Set;
import com.google.common.collect.Sets;

import net.edudb.data_type.DataType;
import net.edudb.data_type.IntegerType;
import net.edudb.ebtree.EBTree;
import net.edudb.expression.*;

public class TableRecord implements Record, Serializable, Cloneable {

	private static final long serialVersionUID = -3305225200308977932L;

	LinkedHashMap<DBColumn, DataType> data;

	public TableRecord() {
		this.data = new LinkedHashMap<>();
	}

	public TableRecord(LinkedHashMap<DBColumn, DataType> data) {
		this.data = data;
	}

	@Override
	public void addValue(DBColumn key, DataType value) {
		data.put(key, value);
	}

	@Override
	public LinkedHashMap<DBColumn, DataType> getData() {
		return data;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Record project(Integer[] projectedColumns) {
		LinkedHashMap<DBColumn, DataType> dataClone = (LinkedHashMap<DBColumn, DataType>) getData().clone();
		Record resultRecord = new TableRecord(dataClone);

		Integer[] columns = new Integer[dataClone.size()];
		int i = 0;
		for (DBColumn column : dataClone.keySet()) {
			columns[i++] = column.getOrder();
		}

		Set<Integer> set1 = Sets.newHashSet(columns);
		Set<Integer> set2 = Sets.newHashSet(projectedColumns);

		Object[] setDifference = Sets.difference(set1, set2).toArray();

		for (Object integer : setDifference) {
			Integer in = (Integer) integer;
			dataClone.remove(new DBColumn(in));
		}

		return resultRecord;
	}

	@Override
	public boolean evaluate(BinaryExpressionTree expressionTree) {
		return expressionTree.evaluate(data);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Record join(Record record) {
		LinkedHashMap<DBColumn, DataType> dataClone = (LinkedHashMap<DBColumn, DataType>) getData().clone();
		final Record resultRecord = new TableRecord(dataClone);

		LinkedHashMap<DBColumn, DataType> data = record.getData();
		final int size = getData().size();
		data.forEach((key, value) -> {
			resultRecord.addValue(new DBColumn(key.getOrder() + size, key.getName(), key.getTableName()), value);
		});

		return resultRecord;
	}

	@Override
	public boolean equates(Record record, Expression expression) {
		DataType leftValue = this.getData().get(expression.getLeftColumn());
		DataType rightValue = record.getData().get(expression.getRightColumn());
		
		return leftValue.compareTo(rightValue) == 0;
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	@Override
	public String toString() {
		return data.toString();
	}

	public static void main(String[] args) throws CloneNotSupportedException {
		// Record r = new TableRecord();
		// DBColumn a = new DBColumn(1, "a", "test");
		// DBColumn b = new DBColumn(2, "b", "test");
		// DBColumn c = new DBColumn(3, "c", "test");
		// r.addValue(a, new IntegerType(10));
		// r.addValue(b, new IntegerType(11));
		// r.addValue(c, new IntegerType(20));

		// BinaryExpressionNode and = new ANDLogicalOperator();
		// and.setLeftChild(new Expression(a, new IntegerType(10),
		// OperatorType.Equal));
		// and.setRightChild(new Expression(b, new IntegerType(1),
		// OperatorType.Equal));
		//
		// BinaryExpressionNode or = new ANDLogicalOperator();
		// or.setRightChild(new Expression(c, new IntegerType(20),
		// OperatorType.Equal));
		//
		// EBTree et = new BinaryExpressionTree(and);
		// et.addNode(or);
		//
		// System.out.println(r.evaluate((BinaryExpressionTree) et));
	}

}
