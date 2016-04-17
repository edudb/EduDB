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
	public void setNextElementInChain(RAMatcherChain chainElement) {
		this.nextElement = chainElement;
	}

	@Override
	public RAMatcherResult match(String string) {
		Matcher matcher = Translator.matcher(string, regex);
		if (matcher.matches()) {
			FilterOperator filterOperator = new FilterOperator();

			ExpressionTree tree = getExpressionTree(matcher.group(2));
			filterOperator.setParameter(tree);

			return new RAMatcherResult(filterOperator, matcher.group(1));
		}
		return this.nextElement.match(string);
	}

	/**
	 * Matches string of the format: #{number}={number}. e.g. #1=2
	 */
	private String genericExpr = "(?:\\#\\d+\\<?\\>?\\=?#?\\d+)";
	/**
	 * Matches string of the format: AND({anything}) or OR({anything})
	 */
	private String binaryExpr = "(?:(?:AND|OR)\\(.*\\))";
	/**
	 * Matches string of the format: #{number}={number} or (AND({anything}) or
	 * OR({anything}))
	 */
	private String argument = "(?:" + genericExpr + "|" + binaryExpr + ")";
	/**
	 * Matches and captures string of the format: #{number}={number}. e.g. #1=2
	 */
	private String capturedConstantExpr = "\\#(\\d+)(\\<?\\>?\\=?)(\\d+)";
	/**
	 * Matches and captures string of the format: #{number}={number}. e.g. #1=2
	 */
	private String capturedColumnExpr = "\\#(\\d+)(\\<?\\>?\\=?)#(\\d+)";
	/**
	 * Matches string of the format: AND(genericExpr or binaryExpr, genericExpr or binaryExpr) or OR(genericExpr or binaryExpr, genericExpr or binaryExpr)
	 * 
	 * e.g. AND(OR(#1=3,#4=1),#2=44)
	 */
	private String capturedBinaryExpr = "(AND|OR)\\(" + "(" + argument + ")" + "," + "(" + argument + ")" + "\\)";

	private ExpressionTree getExpressionTree(String string) {
		Matcher constantExpression = Translator.matcher(string, capturedConstantExpr);
		Matcher columnExpression = Translator.matcher(string, capturedColumnExpr);
		Matcher binaryExpression = Translator.matcher(string, capturedBinaryExpr);

		if (constantExpression.matches()) {
			return new BinaryExpressionTree(getConstantExpression(constantExpression));
		} else if (columnExpression.matches()) {
			return new BinaryExpressionTree(getColumnExpression(columnExpression));
		} else if (binaryExpression.matches()) {
			return new BinaryExpressionTree(getLogicalOperator(binaryExpression));
		}

		return null;
	}

	private Expression getConstantExpression(Matcher matcher) {
		int order = Integer.parseInt(matcher.group(1));
		
		DBColumn column = new DBColumn(order);
		DataType value = new IntegerType(matcher.group(3));
		
		OperatorType operator = getOperator(matcher.group(2));

		Expression expression = new Expression(column, value, operator);
		return expression;
	}
	
	private Expression getColumnExpression(Matcher matcher) {
		int leftOrder = Integer.parseInt(matcher.group(1));
		int rightOrder = Integer.parseInt(matcher.group(3));
		
		DBColumn leftColumn = new DBColumn(leftOrder);
		DBColumn rightColumn = new DBColumn(rightOrder);
		
		OperatorType operator = getOperator(matcher.group(2));

		Expression expression = new Expression(leftColumn, rightColumn, operator);
		return expression;
	}
	
	private OperatorType getOperator(String string) {
		switch (string) {
		case "<>":
			return OperatorType.NotEqual;
		case ">":
			return OperatorType.GreaterThan;
		case "<":
			return OperatorType.LessThan;
		case ">=":
			return OperatorType.GreaterThanOrEqual;
		case "<=":
			return OperatorType.LessThanOrEqual;
		default:
			return OperatorType.Equal;
		}
	}
	
	private LogicalOperator getLogicalOperator(Matcher matcher) {
		Matcher leftExpression = Translator.matcher(matcher.group(2), capturedConstantExpr);
		Matcher leftBinaryExpression = Translator.matcher(matcher.group(2), capturedBinaryExpr);
		
		Matcher rightExpression = Translator.matcher(matcher.group(3), capturedConstantExpr);
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
			operator.setLeftChild(getConstantExpression(leftExpression));
		} else if (leftBinaryExpression.matches()) {
			operator.setLeftChild(getLogicalOperator(leftBinaryExpression));
		}
		
		if (rightExpression.matches()) {
			operator.setRightChild(getConstantExpression(rightExpression));
		} else if (rightBinaryExpression.matches()) {
			operator.setRightChild(getLogicalOperator(rightBinaryExpression));
		}
		
		return operator;
	}
}
