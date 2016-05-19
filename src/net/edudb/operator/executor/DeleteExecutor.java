/*
EduDB is made available under the OSI-approved MIT license.

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

package net.edudb.operator.executor;

/**
 * Executes the SQL DELETE statement.
 * 
 * @author Ahmed Abdul Badie
 *
 */
import net.edudb.expression.BinaryExpressionTree;
import net.edudb.expression.ExpressionTree;
import net.edudb.operator.DeleteOperator;
import net.edudb.operator.Operator;
import net.edudb.relation.Relation;
import net.edudb.relation.RelationIterator;
import net.edudb.structure.Record;

public class DeleteExecutor extends PostOrderOperatorExecutor implements OperatorExecutionChain {
	private OperatorExecutionChain nextElement;

	@Override
	public void setNextElementInChain(OperatorExecutionChain chainElement) {
		this.nextElement = chainElement;
	}

	@Override
	public Relation execute(Operator operator) {
		if (operator instanceof DeleteOperator) {
			DeleteOperator delete = (DeleteOperator) operator;
			ExpressionTree tree = (ExpressionTree) delete.getParameter();
			Relation relation = getChain().execute((Operator) delete.getChild());

			RelationIterator iterator = relation.getIterator();
			while (iterator.hasNext()) {
				Record record = (Record) iterator.next();
				if (record.evaluate((BinaryExpressionTree) tree)) {
					record.delete();
				}
			}
			return relation;
		}
		return nextElement.execute(operator);
	}

}
