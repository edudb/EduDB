/*
EduDB is made available under the OSI-approved MIT license.

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

package dataTypes;

import DBStructure.DBConst;

/**
 * Created by mohamed on 3/23/14.
 */
public class DB_Type {

	/**
	 * @author mohamed
	 */
	public static class DB_Int implements DataType, DBConst {
		/**
		 * @uml.property name="number"
		 */
		public int number;

		public DB_Int(int num) {
			number = num;
		}

		public DB_Int(char num) {
			number = num - '0';
		}

		public DB_Int(String s) {
			number = Integer.parseInt(s);
		}

		public double diff(DataType key) {
			if (key instanceof DB_Int) {
				return number - ((DB_Int) key).number;
			}
			return -1;
		}

		public int compareTo(Object dataType) {
			if (dataType instanceof DB_Int) {
				return number - ((DB_Int) dataType).number;
			}
			return -1;
		}

		@Override
		public boolean equals(Object o) {
			if (o instanceof DB_Int) {
				if (number == ((DB_Int) o).number) {
					return true;
				}
			}
			return false;
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
			System.out.print(number);
		}

		@Override
		public int numOfParameters() {
			return 0;
		}
	}

	public static class DB_Char implements DataType, DBConst {
		public char c;

		public DB_Char(char c) {
			this.c = c;
		}

		public double diff(DataType key) {
			if (key instanceof DB_Char) {
				return c - ((DB_Char) key).c;
			}
			return -1;
		}

		public int compareTo(Object dataType) {
			if (dataType instanceof DB_Char) {
				return c - ((DB_Char) dataType).c;
			}
			return -1;
		}

		@Override
		public boolean equals(Object o) {
			if (o instanceof DB_Char) {
				if (c == ((DB_Char) o).c) {
					return true;
				}
			}
			return false;
		}

		@Override
		public int hashCode() {
			return c;
		}

		@Override
		public void print() {
			System.out.print(c);
		}

		@Override
		public int numOfParameters() {
			return 0;
		}
	}

	public static class DB_String implements DataType {
		public String str;

		public double diff(DataType key) {
			return -1;
		}

		public int compareTo(Object dataType) {
			if (dataType instanceof DB_String) {
				return str.compareTo(((DB_String) dataType).str);
			}
			return -1;
		}

		@Override
		public boolean equals(Object o) {
			if (o instanceof DB_String) {
				if (str.equals(((DB_String) o).str)) {
					return true;
				}
			}
			return false;
		}
	}
}