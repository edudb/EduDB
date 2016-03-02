package net.edudb.DataStructures.EBPartitionedHashTable;

import java.util.ArrayList;

/**
 * @author Ahmed Abdul Badie
 */
public class EBPartitionedHashTable implements EBPartitionedHashIndex {

	/**
	 * Hash table directory
	 */
	private EBDirectory directory;

	/**
	 * Keys indexed
	 */
	private String[] keys;

	/**
	 * Number of bits assigned to each key from the hash code
	 */
	private int bitsAssigned;

	/**
	 * @param keys
	 *            Keys to be partitioned
	 */
	public EBPartitionedHashTable(String[] keys) {
		this.setKeys(keys);
		/**
		 * Each key is assigned two bits in the partition; e.g. if there are two
		 * keys, the hash table will have a directory of 16 entries.
		 */
		bitsAssigned = 2;
		this.directory = new EBDirectory((int) Math.pow(2, bitsAssigned * keys.length), bitsAssigned, 100);
	}
	
	/**
	 * 
	 * @return Key indexed
	 */

	public String[] getKeys() {
		return keys;
	}

	private void setKeys(String[] keys) {
		this.keys = keys;
	}

	private boolean isValidIndex(EBIndex index) {
		if (index.getValues().length != keys.length || index.getPageName() == null) {
			return false;
		}
		return true;
	}

	@Override
	public void addIndex(EBIndex index) {
		if (isValidIndex(index)) {
			this.directory.addIndex(index);
		}
	}

	@Override
	public void updateIndex(EBIndex oldIndex, EBIndex newIndex) {
		if (oldIndex.getValues().length == newIndex.getValues().length && oldIndex.getValues().length == keys.length) {
			this.directory.updateIndex(oldIndex, newIndex);
		}
	}

	@Override
	public ArrayList<EBIndex> getIndex(EBIndex index) {
		if (index.getValues().length == keys.length) {
			return this.directory.getIndex(index);
		}
		return null;
	}

	@Override
	public void deleteIndex(EBIndex index) {
		if (index.getValues().length == keys.length) {
			this.directory.deleteIndex(index);
		}
	}
}
