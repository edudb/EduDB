package net.edudb.data_type;

/**
 * 
 * Used as a placeholder of other types when performing a Filter operation. <br>
 * <br>
 * 
 * ATTENTION <br>
 * <br>
 * 
 * Do not evaluate any expressions against this type. You should transform it
 * into a specific type based on the column it matches. <br>
 * 
 * @author Ahmed Abdul Badie
 *
 */
public class GenericType extends DataType {
	private String value;

	public GenericType(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	@Override
	public double diff(DataType dataType) {
		return 0;
	}

	@Override
	public int compareTo(DataType dataType) {
		return 0;
	}

	@Override
	public String toString() {
		return null;
	}

}
