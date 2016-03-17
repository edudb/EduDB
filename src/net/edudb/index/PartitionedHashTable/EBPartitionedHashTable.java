/*
EduDB is made available under the OSI-approved MIT license.

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

package net.edudb.index.PartitionedHashTable;

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
