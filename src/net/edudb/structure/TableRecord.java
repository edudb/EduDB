/*
EduDB is made available under the OSI-approved MIT license.

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

package net.edudb.structure;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Set;
import com.google.common.collect.Sets;
import net.edudb.data_type.DataType;
import net.edudb.expression.*;

/**
 * 
 * @author Ahmed Abdul Badie
 *
 */
public class TableRecord implements Record, Serializable {

	private static final long serialVersionUID = -3305225200308977932L;

	private LinkedHashMap<Column, DataType> data;
	private boolean deleted;

	public TableRecord() {
		this.data = new LinkedHashMap<>();
	}

	public TableRecord(LinkedHashMap<Column, DataType> data) {
		this.data = data;
	}

	@Override
	public void addValue(Column key, DataType value) {
		data.put(key, value);
	}

	@Override
	public LinkedHashMap<Column, DataType> getData() {
		return data;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Record project(Integer[] projectedColumns) {
		LinkedHashMap<Column, DataType> dataClone = (LinkedHashMap<Column, DataType>) getData().clone();
		Record resultRecord = new TableRecord(dataClone);

		Integer[] columns = new Integer[dataClone.size()];
		int i = 0;
		for (Column column : dataClone.keySet()) {
			columns[i++] = column.getOrder();
		}

		Set<Integer> set1 = Sets.newHashSet(columns);
		Set<Integer> set2 = Sets.newHashSet(projectedColumns);

		Object[] setDifference = Sets.difference(set1, set2).toArray();

		for (Object integer : setDifference) {
			Integer in = (Integer) integer;
			dataClone.remove(new Column(in));
		}

		return resultRecord;
	}

	@Override
	public boolean evaluate(BinaryExpressionTree expressionTree) {
		if (expressionTree == null) {
			return true;
		}
		return expressionTree.evaluate(data);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Record join(Record record) {
		LinkedHashMap<Column, DataType> dataClone = (LinkedHashMap<Column, DataType>) getData().clone();
		final Record resultRecord = new TableRecord(dataClone);

		LinkedHashMap<Column, DataType> data = record.getData();
		final int size = getData().size();
		data.forEach((key, value) -> {
			resultRecord.addValue(
					new Column(key.getOrder() + size, key.getName(), key.getTableName(), key.getTypeName()), value);
		});

		return resultRecord;
	}

	@Override
	public boolean equates(Record record, Expression expression) {
		DataType leftValue = this.getData().get(expression.getLeftColumn());
		DataType rightValue = record.getData().get(expression.getRightColumn());

		return leftValue.compareTo(rightValue) == 0;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Record equiJoin(Record record, Column column) {
		Record joinedRecord = this.join(record);
		LinkedHashMap<Column, DataType> dataClone = (LinkedHashMap<Column, DataType>) joinedRecord.getData().clone();
		int columnToRemoveOrder = this.data.size() + column.getOrder();

		Record resultRecord = new TableRecord();

		// dataClone.remove(new Column(columnToRemoveOrder));

		dataClone.forEach((key, value) -> {
			if (key.getOrder() > columnToRemoveOrder) {
				resultRecord.addValue(
						new Column(key.getOrder() - 1, key.getName(), key.getTableName(), key.getTypeName()), value);
			} else if (key.getOrder() < columnToRemoveOrder) {
				resultRecord.addValue(new Column(key.getOrder(), key.getName(), key.getTableName(), key.getTypeName()),
						value);
			}
		});

		return resultRecord;
	}

	@Override
	public void update(LinkedHashMap<Column, DataType> data) {
		data.forEach((key, value) -> {
			this.data.put(key, value);
		});
	}

	@Override
	public void delete() {
		deleted = true;
	}

	@Override
	public boolean isDeleted() {
		return deleted;
	}

	@Override
	public String toString() {
		return data.toString();
	}

}
