package net.edudb.DataStructures.EBPartitionedHashTable;

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
		return "Values: [" + String.join(",", values) + "]\t"
				+ "Page: " + pageName + "\t"
				+ "Row: " + rowNumber + "\t"
				+ "Deleted: " + isDeleted;
	}
	
	public static void main(String[] args) {
		String[] v1 = {null, null}; 
		EBIndex i1 = new EBIndex(v1, "t", 1);
		
		String[] v2 = {"ahmed", null}; 
		EBIndex i2 = new EBIndex(v2, "t", 1);
		
		System.out.println(i1.equals(i2));
	}
}
