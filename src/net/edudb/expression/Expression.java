/*
EduDB is made available under the OSI-approved MIT license.

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

package net.edudb.expression;

import java.util.LinkedHashMap;

import net.edudb.data_type.DataType;
import net.edudb.ebtree.EBNode;
import net.edudb.structure.DBColumn;

public class Expression implements BinaryExpressionNode {
	private BinaryExpressionNode parent;
	DBColumn column;
	DataType value;
	OperatorType operator;

	public Expression(DBColumn column, DataType value, OperatorType operator) {
		this.column = column;
		this.value = value;
		this.operator = operator;
	}

	@Override
	public BinaryExpressionNode getParent() {
		return parent;
	}

	@Override
	public void setParent(EBNode parent) {
		this.parent = (BinaryExpressionNode) parent;
	}

	@Override
	public BinaryExpressionNode getLeftChild() {
		return null;
	}

	@Override
	public void setLeftChild(EBNode leftChild) {
	}

	@Override
	public BinaryExpressionNode getRightChild() {
		return null;
	}

	@Override
	public void setRightChild(EBNode rightChild) {
	}

	@Override
	public boolean evaluate(LinkedHashMap<DBColumn, DataType> data) {
		switch (operator) {
		case Equal:
			return data.get(column).compareTo(value) == 0;
		case NotEqual:
			return data.get(column).compareTo(value) != 0;
		case GreaterThan:
			return data.get(column).compareTo(value) > 0;
		case LessThan:
			return data.get(column).compareTo(value) < 0;
		case GreaterThanOrEqual:
			return data.get(column).compareTo(value) > 0 || data.get(column).compareTo(value) == 0;
		case LessThanOrEqual:
			return data.get(column).compareTo(value) < 0 || data.get(column).compareTo(value) == 0;
		default:
			return false;
		}
	}
}
