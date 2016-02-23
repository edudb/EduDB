package net.edudb.DataStructures.EBPartitionedHashTable;

import java.util.ArrayList;

/**
 * 
 * @author Ahmed Abdul Badie
 *
 */

public class EBBucketList implements EBPartitionedHashIndex {
	
	/**
	 * ArrayList of connected buckets
	 */
	private ArrayList<EBBucket> buckets;
	
	/**
	 * The current bucket to insert into
	 */
	private EBBucket currentBucket;
	
	/**
	 * Number of indexes the bucket can hold
	 */
	private int bucketCapacity;
	
	public EBBucketList(int bucketCapacity) {
		// TODO Auto-generated constructor stub
		this.bucketCapacity = bucketCapacity;
		this.buckets = new ArrayList<EBBucket>();
		EBBucket bucket = new EBBucket(this.bucketCapacity);
		this.currentBucket = bucket;
		buckets.add(bucket);
	}
	
	/**
	 * 
	 * @return Number of buckets in the list
	 */
	public int buckets() {
		return this.buckets.size();
	}

	@Override
	public void addIndex(EBIndex index) {
		this.currentBucket.addIndex(index);
		if (this.currentBucket.isFull()) {
			EBBucket bucket = new EBBucket(this.bucketCapacity);
			this.currentBucket = bucket;
			buckets.add(bucket);
		}
	}

	@Override
	public void updateIndex(EBIndex oldIndex, EBIndex newIndex) {
		for (EBBucket ebBucket : buckets) {
			ebBucket.updateIndex(oldIndex, newIndex);
		}
	}

	@Override
	public ArrayList<EBIndex> getIndex(EBIndex index) {
		ArrayList<EBIndex> indexList = new ArrayList<EBIndex>();
		for (EBBucket ebBucket : buckets) {
			indexList.addAll(ebBucket.getIndex(index));
		}
		return indexList;
	}

	@Override
	public void deleteIndex(EBIndex index) {
		for (EBBucket ebBucket : buckets) {
			ebBucket.deleteIndex(index);
		}
	}

}
