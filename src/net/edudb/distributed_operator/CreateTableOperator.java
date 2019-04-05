package net.edudb.distributed_operator;

import net.edudb.distributed_operator.parameter.DistributedOperatorParameter;
import net.edudb.distributed_query.UnaryQueryNode;
import net.edudb.ebtree.EBNode;

public class CreateTableOperator implements Operator, UnaryQueryNode {

    private DistributedOperatorParameter parameter;
    private EBNode parent;

    public void setParameter(DistributedOperatorParameter parameter) { this.parameter = parameter; }


    public DistributedOperatorParameter getParameter() { return parameter; }

    @Override
    public void setChild(EBNode child) {
    }

    @Override
    public EBNode getChild() {
        return null;
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
