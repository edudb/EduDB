/*
EduDB is made available under the OSI-approved MIT license.

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

package net.edudb.index.PartitionedHashTable;

/**
 * 
 * @author Ahmed Abdul Badie
 *
 */

public class EBIndex {

	private String[] values;
	private String pageName;
	private int rowNumber;
	private boolean isDeleted;

	public EBIndex(String[] values) {
		this.values = values;
	}

	public EBIndex(String[] values, String pageName, int rowNumber) {
		this.values = values;
		this.setPageName(pageName);
		this.setRowNumber(rowNumber);
		this.setDeleted(false);
	}

	@Override
	public boolean equals(Object object) {
		if (!(object instanceof EBIndex)) {
			return false;
		}

		EBIndex other = (EBIndex) object;
		for (int i = 0; i < values.length; i++) {
			if (other.getValues()[i] != null && this.getValues()[i] != null) {
				if (!other.getValues()[i].equals(this.getValues()[i])) {
					return false;
				}
			}
		}
		return true;
	}

	public String[] getValues() {
		return values;
	}

	public void setValues(String[] values) {
		for (int i = 0; i < values.length; i++) {
			if (values[i] != null) {
				this.values[i] = values[i];
			}
		}
	}

	public String getPageName() {
		return pageName;
	}

	private void setPageName(String pageName) {
		this.pageName = pageName;
	}

	public int getRowNumber() {
		return rowNumber;
	}

	private void setRowNumber(int rowNumber) {
		this.rowNumber = rowNumber;
	}

	public boolean isDeleted() {
		return isDeleted;
	}

	public void setDeleted(boolean isDeleted) {
		this.isDeleted = isDeleted;
	}

	public String toString() {
		return "Values: [" + String.join(",", values) + "]\t" + "Page: " + pageName + "\t" + "Row: " + rowNumber + "\t"
				+ "Deleted: " + isDeleted;
	}
}
