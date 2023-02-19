/*
EduDB is made available under the OSI-approved MIT license.

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

package net.edudb.operator.executor;

import net.edudb.operator.Operator;
import net.edudb.operator.ProjectOperator;
import net.edudb.operator.parameter.ProjectOperatorParameter;
import net.edudb.relation.Relation;
import net.edudb.relation.RelationIterator;
import net.edudb.relation.VolatileRelation;
import net.edudb.structure.Record;

/**
 * Executes the relational algebra Project operator.
 * 
 * @author Ahmed Abdul Badie
 *
 */
public class ProjectExecutor extends PostOrderOperatorExecutor implements OperatorExecutionChain {
	private OperatorExecutionChain nextElement;

	@Override
	public void setNextElementInChain(OperatorExecutionChain chainElement) {
		this.nextElement = chainElement;
	}

	@Override
	public Relation execute(Operator operator) {
		if (operator instanceof ProjectOperator) {
			ProjectOperator projectOperator = (ProjectOperator) operator;
			ProjectOperatorParameter projectedColumns = (ProjectOperatorParameter) projectOperator.getParameter();
			Relation relation = getChain().execute((Operator) projectOperator.getChild());

			RelationIterator relationIterator = relation.getIterator();
			Relation resultRelation = new VolatileRelation();

			while (relationIterator.hasNext()) {
				Record r = (Record) relationIterator.next();
				resultRelation.addRecord(r.project(projectedColumns.getProjectedColumns()));
			}

			return resultRelation;
		}
		return nextElement.execute(operator);
	}

}
