/*
EduDB is made available under the OSI-approved MIT license.

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

package net.edudb.data_type;

import java.io.Serializable;
import net.edudb.server.ServerWriter;
import net.edudb.structure.DBConst;

/**
 * @author mohamed
 */
public class IntegerType implements DataType, DBConst, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3302671401075163802L;
	/**
	 * @uml.property name="number"
	 */
	public Integer number;

	public IntegerType(Integer num) {
		number = num;
	}

	public IntegerType(char num) {
		number = num - '0';
	}

	public IntegerType(String s) {
		number = Integer.parseInt(s);
	}

	public double diff(DataType key) {
		if (key instanceof IntegerType) {
			return number - ((IntegerType) key).number;
		}
		return -1;
	}

	@Override
	public int compareTo(Object dataType) {
		IntegerType type = (IntegerType) dataType;
		return number - type.number;
	}

	@Override
	public boolean equals(Object o) {
		IntegerType type = (IntegerType) o;
		return number == type.number;
	}

	@Override
	public int hashCode() {
		return number;
	}

	/**
	 * @return
	 * @uml.property name="number"
	 */
	public int getNumber() {
		return number;
	}

	public String toString() {
		return number + "";
	}

	@Override
	public void print() {
		ServerWriter.getInstance().write(number);
	}

	@Override
	public int numOfParameters() {
		return 0;
	}
}
