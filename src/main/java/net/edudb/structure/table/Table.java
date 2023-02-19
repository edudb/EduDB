/*
EduDB is made available under the OSI-approved MIT license.

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

package net.edudb.structure.table;

import java.util.LinkedHashMap;

import net.edudb.operator.parameter.OperatorParameter;
import net.edudb.page.PageManager;
import net.edudb.structure.Record;

public interface Table extends OperatorParameter {
	/**
	 * 
	 * @return Name of the table.
	 */
	public String getName();

	/**
	 * 
	 * @return PageManager responsible for the table's set of pages.
	 */
	public PageManager getPageManager();

	/**
	 * Delete all the pages associated with the table from disk.
	 */
	public void deletePages();

	/**
	 * Adds a record to the table/relation.
	 * 
	 * @param record
	 *            The record to add.
	 */
	public void addRecord(Record record);

	/**
	 * 
	 * @param columnTypes
	 *            Linked Hash Map that holds each column name and its type name.
	 */
	public void setColumnTypes(LinkedHashMap<String, String> columnTypes);

	/**
	 * 
	 * @return Linked Hash Map that holds each column name and its type name
	 *         inside the table.
	 */
	public LinkedHashMap<String, String> getColumnTypes();

	/**
	 * Prints the whole pages of a given table/relation. <br>
	 * <br>
	 * <b>ATTENTION</b><br>
	 * <br>
	 * Use only for testing.
	 */
	public void print();
}
