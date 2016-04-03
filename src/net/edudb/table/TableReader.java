package net.edudb.table;

import java.io.IOException;

public interface TableReader {
	
	/**
	 * Loads the table's informations from the disk.
	 * 
	 * @param tableName
	 *            Name of the table.
	 * @return Tabular instance holding the table's information.
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public Tabular read(String tableName) throws IOException, ClassNotFoundException;
	
}
