/*
EduDB is made available under the OSI-approved MIT license.

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

package net.edudb.relational_algebra;

import java.util.regex.Matcher;
import net.edudb.data_type.DataType;
import net.edudb.data_type.GenericType;
import net.edudb.engine.Utility;
import net.edudb.expression.ANDLogicalOperator;
import net.edudb.expression.BinaryExpressionTree;
import net.edudb.expression.Expression;
import net.edudb.expression.ExpressionTree;
import net.edudb.expression.LogicalOperator;
import net.edudb.expression.ORLogicalOperator;
import net.edudb.expression.OperatorType;
import net.edudb.operator.FilterOperator;
import net.edudb.structure.Column;

/**
 * Matches the relational algebra Filter formula.
 * 
 * @author Ahmed Abdul Badie
 *
 */
public class FilterMatcher implements RAMatcherChain {
	private RAMatcherChain nextElement;
	/**
	 * Matches strings of the form: <br>
	 * <br>
	 * <b>Filter(arg0, arg1)</b> <br>
	 * <br>
	 * and captures <b>arg0</b> and <b>arg1</b> in the matcher's groups one and
	 * two, respectively. <b>arg0</b> is the relational algebra formula to
	 * filter the tuples from and <b>arg1</b> is the expression to evaluate the
	 * tuples against.
	 */
	private String regex = "\\AFilter\\((.+)\\,\"(.+)\"\\)\\z";

	@Override
	public void setNextElementInChain(RAMatcherChain chainElement) {
		this.nextElement = chainElement;
	}

	@Override
	public RAMatcherResult match(String string) {
		Matcher matcher = Utility.getMatcher(string, regex);
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
	private String genericExpr = "(?:\\#\\d+\\<?\\>?\\=?#?.+)";
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
	private String capturedConstantExpr = "\\#(\\d+)(\\<?\\>?\\=?)(.+)";
	/**
	 * Matches and captures string of the format: #{number}={number}. e.g. #1=2
	 */
	private String capturedColumnExpr = "\\#(\\d+)(\\<?\\>?\\=?)#(\\d+)";
	/**
	 * Matches string of the format: AND(genericExpr or binaryExpr, genericExpr
	 * or binaryExpr) or OR(genericExpr or binaryExpr, genericExpr or
	 * binaryExpr)
	 * 
	 * e.g. AND(OR(#1=3,#4=1),#2=44)
	 */
	private String capturedBinaryExpr = "(AND|OR)\\(" + "(" + argument + ")" + "," + "(" + argument + ")" + "\\)";

	/**
	 * Creates the expression tree from a given expression string; <b>arg1</b>.
	 * 
	 * @param string
	 *            The expression string to match.
	 * @return The created expression tree.
	 */
	private ExpressionTree getExpressionTree(String string) {
		Matcher constantExpression = Utility.getMatcher(string, capturedConstantExpr);
		Matcher columnExpression = Utility.getMatcher(string, capturedColumnExpr);
		Matcher binaryExpression = Utility.getMatcher(string, capturedBinaryExpr);

		if (columnExpression.matches()) {
			return new BinaryExpressionTree(getColumnExpression(columnExpression));
		} else if (constantExpression.matches()) {
			return new BinaryExpressionTree(getConstantExpression(constantExpression));
		} else if (binaryExpression.matches()) {
			return new BinaryExpressionTree(getLogicalOperator(binaryExpression));
		}

		return null;
	}

	/**
	 * Creates an expression where the left-hand side is a column and the
	 * right-hand side is a value.
	 * 
	 * @param matcher
	 *            The matcher that matched the expression string.
	 * @return The created expression.
	 */
	private Expression getConstantExpression(Matcher matcher) {
		int order = Integer.parseInt(matcher.group(1));

		Column column = new Column(order);
		DataType value = new GenericType(matcher.group(3).replace("\\\"", ""));

		OperatorType operator = getOperator(matcher.group(2));

		Expression expression = new Expression(column, value, operator);
		return expression;
	}

	/**
	 * Creates an expression where both sides of the expression are columns.
	 * 
	 * @param matcher
	 *            The matcher that matched the expression string.
	 * @return The created expression.
	 */
	private Expression getColumnExpression(Matcher matcher) {
		int leftOrder = Integer.parseInt(matcher.group(1));
		int rightOrder = Integer.parseInt(matcher.group(3));

		Column leftColumn = new Column(leftOrder);
		Column rightColumn = new Column(rightOrder);

		OperatorType operator = getOperator(matcher.group(2));

		Expression expression = new Expression(leftColumn, rightColumn, operator);
		return expression;
	}

	/**
	 * Creates an {@link OperatorType} from a string.
	 * 
	 * @param string
	 *            The operator.
	 * @return The type of operator.
	 */
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

	/**
	 * Creates a logical operator that has two children expressions where one or
	 * all are also logical operators. This is done recursively.
	 * 
	 * @param matcher
	 *            The matcher that matched the operator.
	 * @return The logical operator created.
	 */
	private LogicalOperator getLogicalOperator(Matcher matcher) {
		Matcher leftExpression = Utility.getMatcher(matcher.group(2), capturedConstantExpr);
		Matcher leftColumnExpression = Utility.getMatcher(matcher.group(2), capturedColumnExpr);
		Matcher leftBinaryExpression = Utility.getMatcher(matcher.group(2), capturedBinaryExpr);

		Matcher rightExpression = Utility.getMatcher(matcher.group(3), capturedConstantExpr);
		Matcher rightColumnExpression = Utility.getMatcher(matcher.group(3), capturedColumnExpr);
		Matcher rightBinaryExpression = Utility.getMatcher(matcher.group(3), capturedBinaryExpr);

		LogicalOperator operator;
		switch (matcher.group(1)) {
		case "AND":
			operator = new ANDLogicalOperator();
			break;
		default:
			operator = new ORLogicalOperator();
			break;
		}

		if (leftColumnExpression.matches()) {
			operator.setLeftChild(getColumnExpression(leftColumnExpression));
		} else if (leftExpression.matches()) {
			operator.setLeftChild(getConstantExpression(leftExpression));
		} else if (leftBinaryExpression.matches()) {
			operator.setLeftChild(getLogicalOperator(leftBinaryExpression));
		}

		if (rightColumnExpression.matches()) {
			operator.setRightChild(getColumnExpression(rightColumnExpression));
		} else if (rightExpression.matches()) {
			operator.setRightChild(getConstantExpression(rightExpression));
		} else if (rightBinaryExpression.matches()) {
			operator.setRightChild(getLogicalOperator(rightBinaryExpression));
		}

		return operator;
	}
}
