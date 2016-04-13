/*
EduDB is made available under the OSI-approved MIT license.

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

package net.edudb.page;

import net.edudb.structure.Record;

public interface Page extends Recordable {

	/**
	 * 
	 * @return Name of the page.
	 */
	public String getName();

	/**
	 * 
	 * @param index
	 *            Index of record to return.
	 * @return The required record.
	 */
	public Record getRecord(int index);

	/**
	 * 
	 * @return Records inside the page.
	 */
	public Record[] getRecords();

	/**
	 * Adds a record to the page.
	 * 
	 * @param record
	 *            Record to be added to the page.
	 */
	public void addRecord(Record record);

	/**
	 * 
	 * @return Number of records the page can hold.
	 */
	public int capacity();

	/**
	 * 
	 * @return Number of records in the page.
	 */
	public int size();

	public boolean isFull();

	public boolean isEmpty();

	/**
	 * Increments the open count.
	 */
	public void open();

	/**
	 * Decrements the open count;
	 */
	public void close();

	public boolean isOpen();

	/**
	 * Prints the records to the console.
	 */
	public void print();

}
