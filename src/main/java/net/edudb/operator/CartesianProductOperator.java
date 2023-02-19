/*
EduDB is made available under the OSI-approved MIT license.

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

package net.edudb.operator;

import net.edudb.ebtree.EBNode;
import net.edudb.operator.parameter.OperatorParameter;
import net.edudb.query.BinaryQueryNode;
import net.edudb.query.QueryNode;

/**
 * A relational algebra operator, with two children, which is used to
 * compute the Cartesian product of two relations.
 * 
 * @author Ahmed Abdul Badie
 *
 */
public class CartesianProductOperator implements Operator, BinaryQueryNode {
	private OperatorParameter parameter;
	private QueryNode parent;
	private QueryNode leftChild;
	private QueryNode rightChild;

	@Override
	public void setParameter(OperatorParameter parameter) {
		this.parameter = parameter;
	}

	@Override
	public OperatorParameter getParameter() {
		return parameter;
	}

	@Override
	public void setLeftChild(EBNode child) {
		this.leftChild = (QueryNode) child;
	}

	@Override
	public void setRightChild(EBNode child) {
		this.rightChild = (QueryNode) child;
	}

	@Override
	public EBNode getLeftChild() {
		return leftChild;
	}

	@Override
	public EBNode getRightChild() {
		return rightChild;
	}

	@Override
	public void setParent(EBNode parent) {
		this.parent = (QueryNode) parent;
	}

	@Override
	public EBNode getParent() {
		return parent;
	}

}
