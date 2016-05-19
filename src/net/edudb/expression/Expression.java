/*
EduDB is made available under the OSI-approved MIT license.

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

package net.edudb.expression;

import java.util.LinkedHashMap;
import java.util.Map.Entry;
import net.edudb.data_type.DataType;
import net.edudb.data_type.DataTypeFactory;
import net.edudb.data_type.GenericType;
import net.edudb.ebtree.EBNode;
import net.edudb.operator.parameter.OperatorParameter;
import net.edudb.structure.Column;

/**
 * 
 * @author Ahmed Abdul Badie
 *
 */
public class Expression implements BinaryExpressionNode, OperatorParameter {
	private BinaryExpressionNode parent;
	Column leftColumn;
	Column rightColumn;
	DataType value;
	OperatorType operator;

	public Expression(Column leftColumn, DataType value, OperatorType operator) {
		this.leftColumn = leftColumn;
		this.value = value;
		this.operator = operator;
	}

	public Expression(Column leftColumn, Column rightColumn, OperatorType operator) {
		this.leftColumn = leftColumn;
		this.rightColumn = rightColumn;
		this.operator = operator;
	}

	public Column getLeftColumn() {
		return leftColumn;
	}

	public Column getRightColumn() {
		return rightColumn;
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
	public boolean evaluate(LinkedHashMap<Column, DataType> data) {
		/**
		 * The right-hand side of the expression.
		 */
		Object val = data.get(leftColumn);

		/**
		 * The leftColumn is a Column that has only an order. This loop selects
		 * the complete Column which resides in the record. Since relational
		 * algebra operators does not have access to the table itself, but
		 * SQLToAlgebra outputs the order of each column, only the order is
		 * added to the created Column. Columns are hashed based on their order,
		 * therefore, the order is used.
		 */
		for (Entry<Column, DataType> entry : data.entrySet()) {
			if (entry.getKey().equals(leftColumn)) {
				leftColumn = entry.getKey();
				break;
			}
		}

		DataType value = null;
		/**
		 * Checks if the expression type is of the form 'column operator value'.
		 * e.g. a=2.
		 */
		if (this.value != null) {
			/**
			 * Since parsing a relation algebra formula that has an expression
			 * creates a generic data type, it must be transformed into a
			 * defined data type.
			 */
			value = new DataTypeFactory().makeType(leftColumn.getTypeName(), ((GenericType) this.value).getValue());
		}

		/**
		 * Expression in which both sides are columns. e.g. a=b where a,b are
		 * table columns.
		 */
		if (rightColumn != null) {
			int comparisonResult = ((DataType) val).compareTo((DataType) data.get(rightColumn));
			return evaluate(comparisonResult);
		}
		/**
		 * Expression in which the left-hand side is a column and the right-hand
		 * side is a constant value. e.g. a=2.
		 */
		else {
			int comparisonResult = ((DataType) val).compareTo(value);
			return evaluate(comparisonResult);
		}
	}

	/**
	 * Given the type of operator, it returns whether the evaluation holds.
	 * 
	 * @param comparisonResult
	 *            The value to check.
	 * @return The result of the evaluation.
	 */
	private boolean evaluate(int comparisonResult) {
		switch (operator) {
		case Equal:
			return comparisonResult == 0;
		case NotEqual:
			return comparisonResult != 0;
		case GreaterThan:
			return comparisonResult > 0;
		case LessThan:
			return comparisonResult < 0;
		case GreaterThanOrEqual:
			return comparisonResult > 0 || comparisonResult == 0;
		case LessThanOrEqual:
			return comparisonResult < 0 || comparisonResult == 0;
		default:
			return false;
		}
	}

}
