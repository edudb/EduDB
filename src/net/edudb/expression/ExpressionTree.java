package net.edudb.expression;

import java.util.LinkedHashMap;

import net.edudb.data_type.DataType;
import net.edudb.operator.OperatorParameter;
import net.edudb.structure.DBColumn;

public interface ExpressionTree extends OperatorParameter {

	/**
	 * 
	 * @param <T>
	 * @param values
	 *            Values for the tree to be evaluated against.
	 * @return Result of evaluation.
	 */
	public boolean evaluate(LinkedHashMap<DBColumn, DataType> data);

}
