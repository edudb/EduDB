/*
EduDB is made available under the OSI-approved MIT license.

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

package net.edudb.structure;

import java.util.LinkedHashMap;

import net.edudb.data_type.DataType;
import net.edudb.expression.BinaryExpressionTree;
import net.edudb.expression.Expression;

public interface Record {

	/**
	 * Adds a column-value pair to the tuple.
	 * 
	 * @param key
	 *            The colmn to add.
	 * @param value
	 *            The value to add.
	 */
	public void addValue(Column key, DataType value);

	/**
	 * 
	 * @return The tuple.
	 */
	public LinkedHashMap<Column, DataType> getData();

	/**
	 * Projects a set of columns from the caller record.
	 * 
	 * @param projectedColumns
	 *            Columns to project from the caller record.
	 * @return Record containing the projected columns.
	 */
	public Record project(Integer[] projectedColumns);

	/**
	 * Evaluates an expression tree against the caller record.
	 * 
	 * @param expressionTree
	 *            The expression tree to evaluate.
	 * @return The evaluation result of the expression tree against the caller
	 *         record.
	 */
	public boolean evaluate(BinaryExpressionTree expressionTree);

	/**
	 * 
	 * @param record
	 *            The record to join with the caller.
	 * @return The record in which the caller and the record argument are joined
	 *         together.
	 */
	public Record join(Record record);

	/**
	 * 
	 * @param record
	 *            The record to equate against.
	 * @param expression
	 *            The expression in which the columns are evaluated against;
	 *            column = column. For example, a = b.
	 * @return The result of equating the caller record with the argument
	 *         record.
	 */
	public boolean equates(Record record, Expression expression);

	/**
	 * Joins the caller record with the argument record iff they equate to each
	 * other. See {@link #equates(Record, Expression) equates}.
	 * 
	 * @param record
	 *            The record to join with the caller record.
	 * @param column
	 *            The column to remove from the argument record. If there was a
	 *            Project after an EquiJoin, SQLToAlgebra assumes that the
	 *            projected columns does not contain the right column of the
	 *            EquiJoin.
	 * @return
	 */
	public Record equiJoin(Record record, Column column);

	/**
	 * Updates the record.
	 * 
	 * @param data
	 *            The data to update.
	 */
	public void update(LinkedHashMap<Column, DataType> data);

	/**
	 * Deletes the caller record.
	 */
	public void delete();

	/**
	 * 
	 * @return The status of the caller record.
	 */
	public boolean isDeleted();

}
