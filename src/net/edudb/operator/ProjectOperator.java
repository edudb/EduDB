package net.edudb.operator;

import net.edudb.ebtree.EBNode;
import net.edudb.operator.parameter.OperatorParameter;
import net.edudb.query.QueryNode;
import net.edudb.query.UnaryQueryNode;

public class ProjectOperator implements Operator, UnaryQueryNode {
	
	private OperatorParameter parameter;
	private EBNode parent;
	private QueryNode child;

	@Override
	public void setParameter(OperatorParameter parameter) {
		this.parameter = parameter;
	}

	@Override
	public OperatorParameter getParameter() {
		return parameter;
	}

	@Override
	public void setChild(EBNode child) {
		this.child = (QueryNode) child;
	}

	@Override
	public EBNode getChild() {
		return child;
	}

	@Override
	public void setParent(EBNode parent) {
		this.parent = parent;
	}

	@Override
	public EBNode getParent() {
		return parent;
	}

}
