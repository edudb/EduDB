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
import net.edudb.structure.Column;

/**
 * An operator, with two children, that evaluates its children expressions given
 * a logical operator. For example, if an <i><b>AND</b></i> logical operator has
 * two children expressions <b>A</b> and <b>B</b> where <b>A</b> is <i>col =
 * val</i> and <b>B</b> is <i>col > val</i>, then the whole expression is
 * evaluated as <i>col = val <b>AND</b> col > val</i>.<br>
 * <br>
 * 
 * Currently supported logical operators: <b>AND</b> and <b>OR</b>.
 * 
 * 
 * @author Ahmed Abdul Badie
 *
 */
public abstract class LogicalOperator implements BinaryExpressionNode {

	protected BinaryExpressionNode parent;
	protected BinaryExpressionNode leftChild;
	protected BinaryExpressionNode rightChild;

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
		return leftChild;
	}

	@Override
	public void setLeftChild(EBNode leftChild) {
		this.leftChild = (BinaryExpressionNode) leftChild;
	}

	@Override
	public BinaryExpressionNode getRightChild() {
		return rightChild;
	}

	@Override
	public void setRightChild(EBNode rightChild) {
		this.rightChild = (BinaryExpressionNode) rightChild;
	}

	@Override
	public abstract boolean evaluate(LinkedHashMap<Column, DataType> data);

}
