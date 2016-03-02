package net.edudb.DataStructures.EBPartitionedHashTable;

import java.util.ArrayList;

/**
 * 
 * @author Ahmed Abdul Badie
 *
 */

public class EBBucket implements EBPartitionedHashIndex {
	
	/**
	 * Number of indexes the bucket will hold
	 */
	private int pageSize;
	/**
	 * The next free position to insert the index into
	 */
	private int freeSlot;
	/**
	 * Array of indexes the bucket holds
	 */
	private EBIndex[] indexes;
	
	public EBBucket(int pageSize) {
		this.pageSize = pageSize;
		this.indexes = new EBIndex[this.pageSize];
		this.freeSlot = 0;
	}
	
	/**
	 * 
	 * @return Bucket is full of indexes
	 */
	
	public boolean isFull() {
		return this.freeSlot == this.pageSize;
	}
	
	/**
	 * 
	 * @return Bucket has no indexes
	 */
	
	public boolean isEmpty() {
		return this.freeSlot == 0;
	}
	
	/**
	 * 
	 * @return Count of indexes in the bucket
	 */
	
	public int length() {
		return this.freeSlot;
	}
	
	/**
	 * 
	 * @return Number of indexes the bucket can hold
	 */
	
	public int capacity() {
		return this.pageSize;
	}
	
	/**
	 * 
	 * @return Count of not deleted indexes
	 */
	
	public int size() {
		int count = 0;
		for (int i = 0; i < this.freeSlot; ++i) {
			if (!this.indexes[i].isDeleted()) {
				++count;
			}
		}
		return count;
	}
	
	public void print() {
		for (int i = 0; i < this.freeSlot; ++i) {
			EBIndex ebIndex = this.indexes[i];
			System.out.println(ebIndex.toString());
		}
	}

	@Override
	public void addIndex(EBIndex index) {
		this.indexes[freeSlot++] = index;
	}

	@Override
	public void updateIndex(EBIndex oldIndex, EBIndex newIndex) {
		for (int i = 0; i < this.freeSlot; ++i) {
			EBIndex ebIndex = this.indexes[i];
			if (ebIndex.equals(oldIndex) && !ebIndex.isDeleted()) {
				ebIndex.setValues(newIndex.getValues());
			}
		}
	}

	@Override
	public ArrayList<EBIndex> getIndex(EBIndex index) {
		ArrayList<EBIndex> indexList = new ArrayList<EBIndex>();
		for (int i = 0; i < this.freeSlot; ++i) {
			EBIndex ebIndex = this.indexes[i];
			if (ebIndex.equals(index) && !ebIndex.isDeleted()) {
				indexList.add(ebIndex);
			}
		}
		return indexList;
	}

	@Override
	public void deleteIndex(EBIndex index) {
		for (int i = 0; i < this.freeSlot; ++i) {
			EBIndex ebIndex = this.indexes[i];
			if (ebIndex.equals(index) && !ebIndex.isDeleted()) {
				ebIndex.setDeleted(true);
			}
		}
	}

}
