package net.edudb.table;

import java.io.IOException;

public interface TableWriter {

	/**
	 * Persists the table's information to the disk.
	 * 
	 * @throws IOException
	 */
	public void write(Tabular table) throws IOException;

}
