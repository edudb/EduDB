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
 * @author Ahmed Abdul Badie
 *
 */
public class BooleanType extends DataType implements Serializable {
	private boolean bool;

	/**
	 * 
	 */
	private static final long serialVersionUID = -8942317279081394033L;

	@Override
	public double diff(DataType dataType) {
		return -1;
	}

	@Override
	public int compareTo(DataType dataType) {
		BooleanType type = (BooleanType) dataType;
		if (bool == type.bool) {
			return 0;
		}
		return -1;
	}

	@Override
	public boolean equals(Object o) {
		BooleanType type = (BooleanType) o;
		return bool == type.bool;
	}

	public boolean getBoolean() {
		return bool;
	}

	@Override
	public String toString() {
		return bool + "";
	}

}
