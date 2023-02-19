/*
EduDB is made available under the OSI-approved MIT license.

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

package net.edudb.index.PartitionedHashTable;

import java.util.ArrayList;

/**
 * The list that contains the buckets.
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
