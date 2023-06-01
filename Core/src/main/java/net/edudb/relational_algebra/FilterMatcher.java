/*
 *
 * EduDB is made available under the OSI-approved MIT license.
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 * /
 */

package net.edudb.relational_algebra;

import net.edudb.data_type.DataType;
import net.edudb.data_type.GenericType;
import net.edudb.engine.Config;
import net.edudb.engine.DatabaseEngine;
import net.edudb.engine.Utility;
import net.edudb.expression.*;
import net.edudb.operator.FilterOperator;
import net.edudb.operator.parameter.FilterOperatorParameter;
import net.edudb.statistics.Schema;
import net.edudb.structure.Column;

import java.util.regex.Matcher;

/**
 * Matches the relational algebra Filter formula.
 *
 * @author Ahmed Abdul Badie
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
    private static final String REGEX = "\\AFilter\\((.+)\\,\"(.+)\"\\)\\z";

    @Override
    public void setNextElementInChain(RAMatcherChain chainElement) {
        this.nextElement = chainElement;
    }

    @Override
    public RAMatcherResult match(String string) {
        Matcher matcher = Utility.getMatcher(string, REGEX);
        if (!matcher.matches()) {
            return this.nextElement.match(string);
        }

        String tableName = matcher.group(1).split("=")[0];
        String capturedExpression = matcher.group(2);
        ExpressionTree tree = getExpressionTree(capturedExpression);

        Matcher constantExpressionMatcher = Utility.getMatcher(capturedExpression, CAPTURED_CONSTANT_EXPR);
        if (!constantExpressionMatcher.matches()) {
            FilterOperatorParameter parameter = new FilterOperatorParameter(tableName, "", tree);
            FilterOperator filterOperator = new FilterOperator(false);
            filterOperator.setParameter(parameter);
            return new RAMatcherResult(filterOperator, matcher.group(1));
        }

        int columnOrder = Integer.parseInt(constantExpressionMatcher.group(1));
        String columnName = Schema.getInstance().getColumnByOrder(tableName, columnOrder).getName();

        boolean canUseIndex = DatabaseEngine.getInstance()
                .getIndexManager()
                .getIndex(Config.getCurrentWorkspace(), Config.getCurrentDatabaseName(), tableName, columnName)
                .isPresent();

        FilterOperatorParameter parameter = new FilterOperatorParameter(tableName, columnName, tree);

        FilterOperator filterOperator = new FilterOperator(canUseIndex);
        filterOperator.setParameter(parameter);

        return new RAMatcherResult(filterOperator, canUseIndex ? "" : matcher.group(1));
    }

    /**
     * Matches string of the format: #{number}={number}. e.g. #1=2
     */
    private static final String GENERIC_EXPR = "(?:\\#\\d+\\<?\\>?\\=?#?.+)";
    /**
     * Matches string of the format: AND({anything}) or OR({anything})
     */
    private static final String BINARY_EXPR = "(?:(?:AND|OR)\\(.*\\))";
    /**
     * Matches string of the format: #{number}={number} or (AND({anything}) or
     * OR({anything}))
     */
    private static final String ARGUMENT = "(?:" + GENERIC_EXPR + "|" + BINARY_EXPR + ")";
    /**
     * Matches and captures string of the format: #{number}={number}. e.g. #1=2
     */
    private static final String CAPTURED_CONSTANT_EXPR = "\\#(\\d+)(\\<?\\>?\\=?)(.+)";
    /**
     * Matches and captures string of the format: #{number}={number}. e.g. #1=2
     */
    private static final String CAPTURED_COLUMN_EXPR = "\\#(\\d+)(\\<?\\>?\\=?)#(\\d+)";
    /**
     * Matches string of the format: AND(genericExpr or binaryExpr, genericExpr
     * or binaryExpr) or OR(genericExpr or binaryExpr, genericExpr or
     * binaryExpr)
     * <p>
     * e.g. AND(OR(#1=3,#4=1),#2=44)
     */
    private static final String CAPTURED_BINARY_EXPR = "(AND|OR)\\(" + "(" + ARGUMENT + ")" + "," + "(" + ARGUMENT + ")" + "\\)";

    /**
     * Creates the expression tree from a given expression string; <b>arg1</b>.
     *
     * @param string The expression string to match.
     * @return The created expression tree.
     */
    private ExpressionTree getExpressionTree(String string) {
        Matcher constantExpression = Utility.getMatcher(string, CAPTURED_CONSTANT_EXPR);
        Matcher columnExpression = Utility.getMatcher(string, CAPTURED_COLUMN_EXPR);
        Matcher binaryExpression = Utility.getMatcher(string, CAPTURED_BINARY_EXPR);

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
     * @param matcher The matcher that matched the expression string.
     * @return The created expression.
     */
    private Expression getConstantExpression(Matcher matcher) {
        System.out.println("constant " + matcher.group());
        int order = Integer.parseInt(matcher.group(1));

        Column column = new Column(order);
        DataType value = new GenericType(matcher.group(3).replace("\\\"", ""));

        OperatorType operator = getOperator(matcher.group(2));

        return new Expression(column, value, operator);
    }

    /**
     * Creates an expression where both sides of the expression are columns.
     *
     * @param matcher The matcher that matched the expression string.
     * @return The created expression.
     */
    private Expression getColumnExpression(Matcher matcher) {
        System.out.println("column " + matcher.group());
        int leftOrder = Integer.parseInt(matcher.group(1));
        int rightOrder = Integer.parseInt(matcher.group(3));

        Column leftColumn = new Column(leftOrder);
        Column rightColumn = new Column(rightOrder);

        OperatorType operator = getOperator(matcher.group(2));

        return new Expression(leftColumn, rightColumn, operator);
    }

    /**
     * Creates an {@link OperatorType} from a string.
     *
     * @param string The operator.
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
     * @param matcher The matcher that matched the operator.
     * @return The logical operator created.
     */
    private LogicalOperator getLogicalOperator(Matcher matcher) {
        System.out.println("binary " + matcher.group());
        Matcher leftExpression = Utility.getMatcher(matcher.group(2), CAPTURED_CONSTANT_EXPR);
        Matcher leftColumnExpression = Utility.getMatcher(matcher.group(2), CAPTURED_COLUMN_EXPR);
        Matcher leftBinaryExpression = Utility.getMatcher(matcher.group(2), CAPTURED_BINARY_EXPR);

        Matcher rightExpression = Utility.getMatcher(matcher.group(3), CAPTURED_CONSTANT_EXPR);
        Matcher rightColumnExpression = Utility.getMatcher(matcher.group(3), CAPTURED_COLUMN_EXPR);
        Matcher rightBinaryExpression = Utility.getMatcher(matcher.group(3), CAPTURED_BINARY_EXPR);

        LogicalOperator operator;
        if (matcher.group(1).equals("AND")) {
            operator = new ANDLogicalOperator();
        } else {
            operator = new ORLogicalOperator();
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
