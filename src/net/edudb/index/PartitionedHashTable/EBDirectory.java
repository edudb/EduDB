/*
EduDB is made available under the OSI-approved MIT license.

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

package net.edudb.index.PartitionedHashTable;

import java.util.ArrayList;

/**
 * Holds pointers to bucket lists.
 * 
 * @author Ahmed Abdul Badie
 *
 */

public class EBDirectory implements EBPartitionedHashIndex {
	/**
	 * Array of BucketList that holds an ArrayList of buckets
	 */
	private EBBucketList[] bucketListArray;
	/**
	 * Number of bits assigned to each key in the partition
	 */
	private int bitsAssigned;
	/**
	 * Number of indexes each bucket will hold
	 */
	private int pageSize;

	/**
	 * 
	 * @param object
	 *            Object to get the binary hash from
	 * @return The last bitsAssigned bits of the hash
	 */
	private String binaryHashCode(Object object) {
		if (object == null) {
			return character('0', bitsAssigned);
		}
		int hash = object.hashCode();
		String binaryHash = Integer.toBinaryString(hash);
		return lastCharactersFromString(character('0', bitsAssigned) + binaryHash, bitsAssigned);
	}

	/**
	 * 
	 * @param times
	 *            Number of char to return
	 * @return String with n number of a char
	 */
	private String character(char c, int times) {
		String str = "";
		for (int i = 0; i < times; i++) {
			str += c;
		}
		return str;
	}

	/**
	 * 
	 * @param str
	 *            The string to chop
	 * @param length
	 *            Number of characters to return
	 * @return The last n characters of the string
	 * 
	 * @see http://stackoverflow.com/a/8768577/2127376
	 */
	private String lastCharactersFromString(String str, int length) {
		return (str.length() == 0) ? "" : str.substring(Math.max(str.length() - length, 0));
	}

	/**
	 * Tests whether the hash value specifies a unique entry If false, this
	 * means that less keys than indexed were given E.g. Indexed(A,B) -> SELECT
	 * * FROM table where A = 5
	 * 
	 * @param index
	 *            Index to check whether its value will yield a unique entry
	 * @return Unique entry?
	 */
	private boolean isUniqueEntry(EBIndex index) {
		String[] values = index.getValues();
		for (String value : values) {
			if (value == null) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 
	 * @param index
	 *            Required index
	 * @return The entry in which the index is to be found
	 */

	private int getEntryNumber(EBIndex index) {
		String[] values = index.getValues();
		String binary = "";
		for (String value : values) {
			binary += binaryHashCode(value);
		}

		return Integer.parseInt(binary, 2);
	}

	/**
	 * 
	 * If the index is to be found in a set of entries, this mask will help to
	 * specify the exact entries. (If the index is to be found in all the
	 * entries that start with 01, the mask, 1100, will be used with each
	 * entry's position to verify that it is in the required set. Entry 0111 AND
	 * 1100 will output 0100 which means this entry, 0111, is in the set)
	 * 
	 * @param index
	 *            Required index
	 * @return Mask to be ANDed with each entry number
	 */

	private int getMask(EBIndex index) {
		String[] values = index.getValues();
		String binary = "";
		for (String value : values) {
			if (value == null) {
				binary += character('0', bitsAssigned);
			} else {
				binary += character('1', bitsAssigned);
			}
		}

		return Integer.parseInt(binary, 2);
	}

	/**
	 * Two indexes have equivalent hashes if the both non-null values hash to
	 * the same value, i.e. updated index should stay in the same bucket
	 * 
	 * @return Whether both indexes have equivalent hashes
	 */
	private boolean areEquivalentHashes(EBIndex oldIndex, EBIndex newIndex) {
		String[] oldValues = oldIndex.getValues();
		String[] newValues = newIndex.getValues();

		for (int i = 0; i < newValues.length; i++) {
			if (oldValues[i] != null && newValues[i] != null) {
				if (!binaryHashCode(oldIndex).equals(binaryHashCode(newIndex))) {
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * Checks if both indexes have exactly the same values
	 * 
	 * @param oldIndex
	 * @param newIndex
	 * @return Identical indexes
	 */
	private boolean identicalIndexes(EBIndex oldIndex, EBIndex newIndex) {
		if (!oldIndex.getValues().equals(newIndex.getValues())) {
			return false;
		}
		return true;
	}

	/**
	 * @param numberOfEntries
	 *            Number of entries in the directory
	 * @param bitsAssigned
	 *            Number of bits assigned to each key partition
	 * @param pageSize
	 *            Number of indexes each bucket will hold
	 * 
	 */
	public EBDirectory(int numberOfEntries, int bitsAssigned, int pageSize) {
		this.bitsAssigned = bitsAssigned;
		this.bucketListArray = new EBBucketList[numberOfEntries];
		this.pageSize = pageSize;
	}

	/**
	 * 
	 * @return The number of entries in the directory
	 */
	public int length() {
		return bucketListArray.length;
	}

	@Override
	public void addIndex(EBIndex index) {
		int entryNumber = getEntryNumber(index);

		EBBucketList list = bucketListArray[entryNumber];
		if (list == null) {
			list = bucketListArray[entryNumber] = new EBBucketList(this.pageSize);
		}

		list.addIndex(index);
	}

	@Override
	public void updateIndex(EBIndex oldIndex, EBIndex newIndex) {
		if (identicalIndexes(oldIndex, newIndex)) {
			return;
		}

		int entryNumber = getEntryNumber(oldIndex);

		EBIndex indexToDelete = new EBIndex(oldIndex.getValues().clone());

		if (isUniqueEntry(oldIndex)) {
			EBBucketList list = bucketListArray[entryNumber];
			if (areEquivalentHashes(oldIndex, newIndex)) {
				if (list != null) {
					list.updateIndex(oldIndex, newIndex);
				}
			}
		}
		if (!isUniqueEntry(oldIndex) || !areEquivalentHashes(oldIndex, newIndex)) {
			ArrayList<EBIndex> indexList = this.getIndex(oldIndex);
			for (EBIndex ebIndex : indexList) {
				ebIndex.setValues(newIndex.getValues());
				this.addIndex(ebIndex);
			}
			this.deleteIndex(indexToDelete);
		}

	}

	@Override
	public ArrayList<EBIndex> getIndex(EBIndex index) {
		ArrayList<EBIndex> indexList = new ArrayList<>();
		int entryNumber = getEntryNumber(index);
		if (isUniqueEntry(index)) {
			EBBucketList list = bucketListArray[entryNumber];
			if (list != null) {
				indexList = list.getIndex(index);
			}
		} else {
			int mask = getMask(index);
			for (int i = 0; i < bucketListArray.length; i++) {
				if ((mask & i) == entryNumber) {
					EBBucketList list = bucketListArray[i];
					if (list != null) {
						indexList.addAll(list.getIndex(index));
					}
				}
			}
		}
		return indexList;
	}

	@Override
	public void deleteIndex(EBIndex index) {
		int entryNumber = getEntryNumber(index);
		if (isUniqueEntry(index)) {
			EBBucketList list = bucketListArray[entryNumber];
			if (list != null) {
				list.deleteIndex(index);
			}
		} else {
			int mask = getMask(index);
			for (int i = entryNumber; i < bucketListArray.length; i++) {
				if ((mask & i) == entryNumber) {
					EBBucketList list = bucketListArray[i];
					if (list != null) {
						bucketListArray[i].deleteIndex(index);
					}
				}
			}
		}

	}

}
