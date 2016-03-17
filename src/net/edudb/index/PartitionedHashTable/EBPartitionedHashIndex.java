/*
EduDB is made available under the OSI-approved MIT license.

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

package net.edudb.index.PartitionedHashTable;

import java.util.ArrayList;

/**
 * 
 * @author Ahmed Abdul Badie
 *
 */

public interface EBPartitionedHashIndex {

	/**
	 * 
	 * Adds a new index to the Partitioned Hash Table.
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
