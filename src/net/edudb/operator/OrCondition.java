/*
EduDB is made available under the OSI-approved MIT license.

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

package net.edudb.operator;

import net.edudb.server.ServerWriter;
import net.edudb.structure.DBRecord;

/**
 * Created by mohamed on 4/19/14.
 */
public class OrCondition implements DBMulCondition {

	/**
	 * @uml.property name="condition1"
	 * @uml.associationEnd
	 */
	DBCond condition1;
	/**
	 * @uml.property name="condition2"
	 * @uml.associationEnd
	 */
	DBCond condition2;

	public int numOfParameters() {
		return 2;
	}

	@Override
	public void print() {
		ServerWriter.getInstance().writeln("OR");
	}

	@Override
	public void setParameter(DBCond param) {
		if (condition1 == null) {
			condition1 = param;
		} else {
			condition2 = param;
		}
	}

	public String toString() {
		return "or( " + condition1.toString() + ", " + condition2.toString() + ") ";
	}

	@Override
	public DBCond[] getChildren() {
		return new DBCond[] { condition1, condition2 };
	}

	@Override
	public boolean evaluate(DBRecord dbRecord) {
		return condition1.evaluate(dbRecord) || condition2.evaluate(dbRecord);
	}
}
