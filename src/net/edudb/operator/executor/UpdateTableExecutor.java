/*
EduDB is made available under the OSI-approved MIT license.

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

package net.edudb.operator.executor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import net.edudb.data_type.DataType;
import net.edudb.data_type.DataTypeFactory;
import net.edudb.expression.BinaryExpressionTree;
import net.edudb.expression.ExpressionTree;
import net.edudb.operator.Operator;
import net.edudb.operator.UpdateTableOperator;
import net.edudb.operator.parameter.UpdateTableOperatorParameter;
import net.edudb.relation.Relation;
import net.edudb.relation.RelationIterator;
import net.edudb.statistics.Schema;
import net.edudb.structure.Column;
import net.edudb.structure.Record;

/**
 * Executes the SQL UPDATE statement.
 * 
 * @author Ahmed Abdul Badie
 *
 */
public class UpdateTableExecutor extends PostOrderOperatorExecutor implements OperatorExecutionChain {
	private OperatorExecutionChain nextElement;

	@Override
	public void setNextElementInChain(OperatorExecutionChain chainElement) {
		this.nextElement = chainElement;
	}

	@Override
	public Relation execute(Operator operator) {
		if (operator instanceof UpdateTableOperator) {
			UpdateTableOperator update = (UpdateTableOperator) operator;
			UpdateTableOperatorParameter parameter = (UpdateTableOperatorParameter) update.getParameter();

			HashMap<String, String> assignments = parameter.getAssignmentList();
			ExpressionTree tree = parameter.getExpressionTree();
			String tableName = parameter.getTableName();

			LinkedHashMap<Column, DataType> data = new LinkedHashMap<>();
			DataTypeFactory typeFactory = new DataTypeFactory();
			ArrayList<Column> columns = Schema.getInstance().getColumns(tableName);
			for (Column column : columns) {
				String columnName = column.getName();
				String assignmentValue = assignments.get(columnName);
				if (assignments.get(columnName) != null) {
					data.put(column, typeFactory.makeType(column.getTypeName(), assignmentValue));
				}
			}

			Relation relation = getChain().execute((Operator) update.getChild());

			RelationIterator iterator = relation.getIterator();
			while (iterator.hasNext()) {
				Record record = (Record) iterator.next();
				if (record.evaluate((BinaryExpressionTree) tree)) {
					record.update(data);
				}
			}
			return relation;
		}
		return nextElement.execute(operator);
	}

}
