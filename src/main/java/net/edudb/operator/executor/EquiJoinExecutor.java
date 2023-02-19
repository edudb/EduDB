/*
EduDB is made available under the OSI-approved MIT license.

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

package net.edudb.operator.executor;

/**
 * Executes the relational algebra EquiJoin operator.
 */
import net.edudb.expression.Expression;
import net.edudb.operator.EquiJoinOperator;
import net.edudb.operator.Operator;
import net.edudb.relation.Relation;
import net.edudb.relation.RelationIterator;
import net.edudb.relation.VolatileRelation;
import net.edudb.structure.Column;
import net.edudb.structure.Record;

public class EquiJoinExecutor extends PostOrderOperatorExecutor implements OperatorExecutionChain {
	private OperatorExecutionChain nextElement;

	@Override
	public void setNextElementInChain(OperatorExecutionChain chainElement) {
		this.nextElement = chainElement;
	}

	@Override
	public Relation execute(Operator operator) {
		if (operator instanceof EquiJoinOperator) {
			EquiJoinOperator equiOperator = (EquiJoinOperator) operator;
			Relation leftRelation = getChain().execute((Operator) equiOperator.getLeftChild());
			Relation rightRelation = getChain().execute((Operator) equiOperator.getRightChild());

			Expression expression = (Expression) equiOperator.getParameter();
			Column rightColumn = expression.getRightColumn();

			Relation resultRelation = new VolatileRelation();

			RelationIterator leftIterator = leftRelation.getIterator();
			RelationIterator rightIterator = rightRelation.getIterator();

			Record leftRecord, rightRecord;
			while (leftIterator.hasNext()) {
				leftRecord = leftIterator.next();
				while (rightIterator.hasNext()) {
					rightRecord = rightIterator.next();
					if (leftRecord.equates(rightRecord, expression)) {
						Record resultRecord = leftRecord.equiJoin(rightRecord, rightColumn);
						resultRelation.addRecord(resultRecord);
					}
				}
				rightIterator = rightRelation.getIterator();
			}

			return resultRelation;
		}
		return nextElement.execute(operator);
	}

}
