/*
EduDB is made available under the OSI-approved MIT license.

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

package operators;

import java.util.ArrayList;

import DBStructure.DBColumn;

/**
 * @author mohamed
 */
public class SelectColumns implements DBParameter {

	/**
	 * @uml.property name="columns"
	 * @uml.associationEnd multiplicity="(0 -1)"
	 *                     elementType="DBStructure.DBColumn"
	 */
	ArrayList<DBColumn> columns;

	public SelectColumns(ArrayList<DBColumn> columns) {
		this.columns = columns;
	}

	@Override
	public void print() {
		String outString = " ";
		for (int i = 0; i < columns.size(); i++) {
			outString += columns.get(i).toString() + ",";
		}
	}

	public ArrayList<DBColumn> getColumns() {
		return columns;
	}

	@Override
	public int numOfParameters() {
		return 0;
	}

	@Override
	public String toString() {
		String outString = " ";
		for (int i = 0; i < columns.size(); i++) {
			outString += columns.get(i).toString() + ",";
		}
		return outString;
	}

	public void union(SelectColumns selectColumns) {
		ArrayList<DBColumn> dbColumns = selectColumns.getDBColumns();
		for (int i = 0; i < dbColumns.size(); i++) {
			if (columns.indexOf(dbColumns.get(i)) == -1) {
				columns.add(dbColumns.get(i));
			}
		}
	}

	private ArrayList<DBColumn> getDBColumns() {
		return columns;
	}
}
