/*
EduDB is made available under the OSI-approved MIT license.

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

package net.edudb.plan;

import net.edudb.expression.BinaryExpressionTree;
import net.edudb.expression.ExpressionTree;
import net.edudb.operator.FilterOperator;
import net.edudb.operator.RelationOperator;
import net.edudb.operator.UpdateTableOperator;
import net.edudb.operator.parameter.RelationOperatorParameter;
import net.edudb.operator.parameter.UpdateTableOperatorParameter;
import net.edudb.query.QueryTree;
import net.edudb.relational_algebra.Translator;
import net.edudb.statement.SQLStatement;
import net.edudb.statement.SQLUpdateStatement;
import net.edudb.statistics.Schema;

/**
 * A plan to update data in a table.
 * 
 * @author Ahmed Abdul Badie
 *
 */
public class UpdatePlan extends Plan {

	@Override
	public QueryTree makePlan(SQLStatement sqlStatement) {
		SQLUpdateStatement statement = (SQLUpdateStatement) sqlStatement;

		if (!Schema.getInstance().chekTableExists(statement.getTableName())) {
			return null;
		}

		QueryTree plan = null;

		UpdateTableOperator operator = new UpdateTableOperator();

		RelationOperatorParameter parameter = new RelationOperatorParameter(statement.getTableName());

		/**
		 * Creates a relation operator to be the child of the update operator.
		 */
		RelationOperator relation = new RelationOperator();
		relation.setParameter(parameter);
		operator.setChild(relation);

		ExpressionTree expressionTree = null;
		if (statement.getWhereClause() != null) {
			/**
			 * This block of code is used to get the expression tree from the
			 * update statement's where clause.
			 *
			 * Since there is no other way to do it, this is just a "hack" to
			 * get things going.
			 *
			 * This create a query tree that has only one node, filter node,
			 * that has an expression tree as its parameter. This expression
			 * tree is used as a parameter to the update table operator.
			 */
			Translator translator = new Translator();
			String ra = translator
					.translate("select * from " + statement.getTableName() + " " + statement.getWhereClause());
			QueryTree queryTree = translator.processRelationalAlgebra(ra);
			FilterOperator filter = (FilterOperator) queryTree.getRoot();
			expressionTree = (BinaryExpressionTree) filter.getParameter();
		}

		UpdateTableOperatorParameter updateParameter = new UpdateTableOperatorParameter(statement.getAssignemnts(),
				expressionTree, statement.getTableName());
		operator.setParameter(updateParameter);

		plan = new QueryTree(operator);

		return plan;
	}
}
