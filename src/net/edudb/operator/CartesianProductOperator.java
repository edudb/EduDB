package net.edudb.operator;

import net.edudb.ebtree.EBNode;
import net.edudb.query.BinaryQueryNode;
import net.edudb.query.QueryNode;

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
