package net.edudb.relational_algebra;

import java.util.regex.Matcher;
import net.edudb.data_type.DataType;
import net.edudb.data_type.IntegerType;
import net.edudb.expression.ANDLogicalOperator;
import net.edudb.expression.BinaryExpressionTree;
import net.edudb.expression.Expression;
import net.edudb.expression.ExpressionTree;
import net.edudb.expression.LogicalOperator;
import net.edudb.expression.ORLogicalOperator;
import net.edudb.expression.OperatorType;
import net.edudb.operator.FilterOperator;
import net.edudb.structure.DBColumn;

public class FilterMatcher implements RAMatcherChain {
	private RAMatcherChain nextElement;
	private String regex = "\\AFilter\\((.+)\\,\"(.+)\"\\)\\z";

	@Override
	public void setNextInChain(RAMatcherChain chainElement) {
		this.nextElement = chainElement;
	}

	@Override
	public RAMatcherResult parse(String string) {
		Matcher matcher = Translator.matcher(string, regex);
		if (matcher.matches()) {
			FilterOperator filter = new FilterOperator();

			ExpressionTree tree = getExpressionTree(matcher.group(2));
			filter.setParameter(tree);

			return new RAMatcherResult(filter, matcher.group(1));
		}
		return this.nextElement.parse(string);
	}

	/**
	 * Matches string of the format: #{number}={number}. e.g. #1=2
	 */
	private String expr = "(?:\\#\\d+\\<?\\>?\\=?\\d+)";
	/**
	 * Matches string of the format: AND({anything}) or OR({anything})
	 */
	private String binaryExpr = "(?:(?:AND|OR)\\(.*\\))";
	/**
	 * Matches string of the format: #{number}={number} or (AND({anything}) or
	 * OR({anything}))
	 */
	private String argument = "(?:" + expr + "|" + binaryExpr + ")";
	/**
	 * Matches and captures string of the format: #{number}={number}. e.g. #1=2
	 */
	private String capturedExpr = "\\#(\\d+)(\\<?\\>?\\=?)(\\d+)";
	/**
	 * Matches string of the format: AND(expr or binaryExpr, expr or binaryExpr) or OR(expr or binaryExpr, expr or binaryExpr)
	 * 
	 * e.g. AND(OR(#1=3,#4=1),#2=44)
	 */
	private String capturedBinaryExpr = "(AND|OR)\\(" + "(" + argument + ")" + "," + "(" + argument + ")" + "\\)";

	private ExpressionTree getExpressionTree(String string) {
		Matcher expression = Translator.matcher(string, capturedExpr);
		Matcher binaryExpression = Translator.matcher(string, capturedBinaryExpr);

		if (expression.matches()) {
			return new BinaryExpressionTree(getExpression(expression));
		} else if (binaryExpression.matches()) {
			return new BinaryExpressionTree(getLogicalOperator(binaryExpression));
		}

		return null;
	}

	private Expression getExpression(Matcher matcher) {
		int order = Integer.parseInt(matcher.group(1));
		DBColumn column = new DBColumn(order);
		DataType value = new IntegerType(matcher.group(3));
		OperatorType operator;
		switch (matcher.group(2)) {
		case "<>":
			operator = OperatorType.NotEqual;
			break;
		case ">":
			operator = OperatorType.GreaterThan;
			break;
		case "<":
			operator = OperatorType.LessThan;
			break;
		case ">=":
			operator = OperatorType.GreaterThanOrEqual;
			break;
		case "<=":
			operator = OperatorType.LessThan;
			break;
		default:
			operator = OperatorType.Equal;
		}

		Expression expression = new Expression(column, value, operator);
		return expression;
	}
	
	private LogicalOperator getLogicalOperator(Matcher matcher) {
		Matcher leftExpression = Translator.matcher(matcher.group(2), capturedExpr);
		Matcher leftBinaryExpression = Translator.matcher(matcher.group(2), capturedBinaryExpr);
		
		Matcher rightExpression = Translator.matcher(matcher.group(3), capturedExpr);
		Matcher rightBinaryExpression = Translator.matcher(matcher.group(3), capturedBinaryExpr);
		
		LogicalOperator operator;
		switch (matcher.group(1)) {
		case "AND":
			operator = new ANDLogicalOperator();
			break;
		default:
			operator = new ORLogicalOperator();
			break;
		}
		
		if (leftExpression.matches()) {
			operator.setLeftChild(getExpression(leftExpression));
		} else if (leftBinaryExpression.matches()) {
			operator.setLeftChild(getLogicalOperator(leftBinaryExpression));
		}
		
		if (rightExpression.matches()) {
			operator.setRightChild(getExpression(rightExpression));
		} else if (rightBinaryExpression.matches()) {
			operator.setRightChild(getLogicalOperator(rightBinaryExpression));
		}
		
		return operator;
	}
}
