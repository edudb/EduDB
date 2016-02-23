package net.edudb.DataStructures.EBPartitionedHashTable;

import java.util.ArrayList;

/**
 * 
 * @author Ahmed Abdul Badie
 *
 */

public interface EBPartitionedHashIndex {

	/**
	 * 
	 * Adds a new to the Partitioned Hash Table.
	 * 
	 * @param index
	 *            Index to be added to Partitioned Hash Table. The index's
	 *            values count must equal the count of the indexed keys
	 */

	public void addIndex(EBIndex index);

	/**
	 * 
	 * If more than a single index matches the required index, all of them will
	 * be updated.
	 * 
	 * @param oldIndex
	 *            Index to be updated
	 * @param newIndex
	 *            Index to replace the oldIndex
	 */

	public void updateIndex(EBIndex oldIndex, EBIndex newIndex);

	/**
	 * 
	 * Find the indexes that match the required index. If more than a single
	 * index is found, all of them will be returned
	 * 
	 * @param index
	 *            Index to retrieve
	 * @return An ArrayList of matching indexes
	 */

	public ArrayList<EBIndex> getIndex(EBIndex index);

	/**
	 * 
	 * If more than a single index matches the required index, all of them will
	 * be deleted.
	 * 
	 * @param index
	 *            Index to delete.
	 */

	public void deleteIndex(EBIndex index);

}
