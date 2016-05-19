/*
EduDB is made available under the OSI-approved MIT license.

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

package net.edudb.data_type;

import java.io.Serializable;

/**
 * Created by mohamed on 3/23/14.
 */
public class DBType {
	public static class DB_Char extends DataType implements Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = 7216756328882699266L;
		public char character;

		public DB_Char(char c) {
			this.character = c;
		}

		public double diff(DataType key) {
			if (key instanceof DB_Char) {
				return character - ((DB_Char) key).character;
			}
			return -1;
		}

		public int compareTo(DataType dataType) {
			if (dataType instanceof DB_Char) {
				return character - ((DB_Char) dataType).character;
			}
			return -1;
		}

		@Override
		public boolean equals(Object o) {
			if (o instanceof DB_Char) {
				if (character == ((DB_Char) o).character) {
					return true;
				}
			}
			return false;
		}

		@Override
		public int hashCode() {
			return character;
		}

		@Override
		public String toString() {
			return character + "";
		}
	}
	
}