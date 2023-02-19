/*
EduDB is made available under the OSI-approved MIT license.

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

package net.edudb.data_type;

/**
 * 
 * @author Ahmed Abdul Badie
 *
 */
public abstract class DataType implements Comparable<DataType> {

	/**
	 * This method is used in the {@link net.edudb.index.kDTree}.
	 * 
	 * @param dataType
	 * @return
	 */
	public abstract double diff(DataType dataType);

	public abstract int compareTo(DataType dataType);

	public abstract String toString();

	/**
	 * Checks whether a given type is supported.
	 * 
	 * @param typeName
	 *            The type to check if it is supported.
	 * @return The support state of the given type.
	 */
	public static boolean isSupported(String typeName) {
		switch (typeName.toLowerCase()) {
		case "bool":
		case "boolean":
		case "decimal":
		case "integer":
		case "timestamp":
		case "datetime":
		case "varchar":
			return true;
		default:
			return false;
		}
	}
}
