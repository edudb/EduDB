/*
EduDB is made available under the OSI-approved MIT license.

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

package net.edudb.index.PartitionedHashTable;

import java.util.ArrayList;

import net.edudb.server.ServerWriter;

/**
 * The container that holds the data.
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
	 * @return Bucket is full of indexes.
	 */
	
	public boolean isFull() {
		return this.freeSlot == this.pageSize;
	}
	
	/**
	 * 
	 * @return Bucket has no indexes.
	 */
	
	public boolean isEmpty() {
		return this.freeSlot == 0;
	}
	
	/**
	 * 
	 * @return Count of indexes in the bucket.
	 */
	
	public int length() {
		return this.freeSlot;
	}
	
	/**
	 * 
	 * @return Number of indexes the bucket can hold.
	 */
	
	public int capacity() {
		return this.pageSize;
	}
	
	/**
	 * 
	 * @return Count of not deleted indexes.
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
			ServerWriter.getInstance().writeln(ebIndex.toString());
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
