/*
EduDB is made available under the OSI-approved MIT license.

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

package net.edudb.data_type;

import java.io.Serializable;

/**
 *
 * @author mohamed
 * @author Ahmed Abdul Badie
 *
 */
public class IntegerType extends DataType implements Serializable {

	private static final long serialVersionUID = 3302671401075163802L;
	/**
	 * @uml.property name="integer"
	 */
	private final Integer integer;

	public IntegerType(Integer integer) {
		this.integer = integer;
	}

	public double diff(DataType key) {
		IntegerType type = (IntegerType) key;
		return integer - type.integer;
	}

	@Override
	public int compareTo(DataType dataType) {
		IntegerType type = (IntegerType) dataType;
		return integer.compareTo(type.integer);
	}

	@Override
	public boolean equals(Object o) {
		IntegerType type = (IntegerType) o;
		return integer.equals(type.integer);
	}

	@Override
	public int hashCode() {
		return integer;
	}

	/**
	 * @return
	 * @uml.property name="integer"
	 */
	public int getInteger() {
		return integer;
	}

	public String toString() {
		return integer.toString();
	}
}
