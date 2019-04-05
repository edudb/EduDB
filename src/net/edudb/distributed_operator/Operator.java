package net.edudb.distributed_operator;
import net.edudb.distributed_operator.parameter.DistributedOperatorParameter;

public interface Operator {

    /**
     * Sets the parameter of an operator.
     *
     * @param parameter
     *            The parameter to set.
     */
    public void setParameter(DistributedOperatorParameter parameter);

    /**
     *
     * @return The parameter of the operator.
     */
    public DistributedOperatorParameter getParameter();
}
