/*
 *
 * EduDB is made available under the OSI-approved MIT license.
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 * /
 */

package net.edudb.operator.executor;

import net.edudb.data_type.DataType;
import net.edudb.data_type.GenericType;
import net.edudb.data_type.VarCharType;
import net.edudb.engine.Config;
import net.edudb.engine.DatabaseEngine;
import net.edudb.expression.BinaryExpressionTree;
import net.edudb.expression.Expression;
import net.edudb.expression.ExpressionTree;
import net.edudb.index.Index;
import net.edudb.operator.FilterOperator;
import net.edudb.operator.Operator;
import net.edudb.operator.parameter.FilterOperatorParameter;
import net.edudb.relation.Relation;
import net.edudb.relation.RelationIterator;
import net.edudb.relation.VolatileRelation;
import net.edudb.structure.Record;

import java.util.ArrayList;
import java.util.Set;

public class IndexFilterExecutor extends PostOrderOperatorExecutor implements OperatorExecutionChain {

    private OperatorExecutionChain nextElement;

    @Override
    public void setNextElementInChain(OperatorExecutionChain chainElement) {
        this.nextElement = chainElement;
    }


    @Override
    public Relation execute(Operator operator) {
        if (operator instanceof FilterOperator filter && filter.isIndexFilter()) {
            System.out.println("Using Index");
            FilterOperatorParameter indexFilterOperator = (FilterOperatorParameter) filter.getParameter();
            String workspaceName = Config.getCurrentWorkspace();
            String databaseName = Config.getCurrentDatabaseName();
            String tableName = indexFilterOperator.tableName();
            String columnName = indexFilterOperator.columnName();
            ExpressionTree expressionTree = indexFilterOperator.expressionTree();

            Expression expression = (Expression) ((BinaryExpressionTree) expressionTree).getRoot();
            String value = ((GenericType) expression.getValue()).getValue();

            Index<DataType> index = DatabaseEngine.getInstance().getIndexManager().getIndex(workspaceName, databaseName, tableName, columnName).get();

            Set<String> pages = index.search(new VarCharType(value));

            Relation resultRelation = new VolatileRelation();

            try (RelationIterator relationIterator = new RelationIterator(new ArrayList<>(pages))) {
                while (relationIterator.hasNext()) {
                    Record r = relationIterator.next();
                    if (r.evaluate((BinaryExpressionTree) expressionTree)) {
                        resultRelation.addRecord(r);
                    }
                }
            }

            return resultRelation;

        }
        return nextElement.execute(operator);
    }
}
