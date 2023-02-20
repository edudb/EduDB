package net.edudb.operator.parameter;

import java.util.HashMap;
import net.edudb.expression.ExpressionTree;

/**
 * The parameter associated with the update table operator.
 *
 * @author Ahmed Abdul Badie
 *
 */
public class UpdateTableOperatorParameter implements OperatorParameter {
	private final HashMap<String, String> assignments;
	private final ExpressionTree expressionTree;
	private final String tableName;

	public UpdateTableOperatorParameter(HashMap<String, String> assignments, ExpressionTree expressionTree,
			String tableName) {
		this.assignments = assignments;
		this.expressionTree = expressionTree;
		this.tableName = tableName;
	}

	public HashMap<String, String> getAssignmentList() {
		return assignments;
	}

	public ExpressionTree getExpressionTree() {
		return expressionTree;
	}

	public String getTableName() {
		return tableName;
	}
}
