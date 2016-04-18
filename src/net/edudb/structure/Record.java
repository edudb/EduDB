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

	public void addValue(Column key, DataType value);
	
	public LinkedHashMap<Column, DataType> getData();
	
	public Record project(Integer[] projectedColumns);
	
	public boolean evaluate(BinaryExpressionTree expressionTree);
	
	public Record join(Record record);
	
	public boolean equates(Record record, Expression expression);
	
	public void update(LinkedHashMap<Column, DataType> data);
	
	public void delete();
	
	public boolean isDeleted();
	
}
