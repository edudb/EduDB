package net.edudb.expression;

import java.util.LinkedHashMap;

import net.edudb.data_type.DataType;
import net.edudb.operator.parameter.OperatorParameter;
import net.edudb.structure.Column;

public interface ExpressionTree extends OperatorParameter {

	/**
	 * 
	 * @param <T>
	 * @param values
	 *            Values for the tree to be evaluated against.
	 * @return Result of evaluation.
	 */
	public boolean evaluate(LinkedHashMap<Column, DataType> data);

}
