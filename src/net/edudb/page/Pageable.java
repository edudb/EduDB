/*
EduDB is made available under the OSI-approved MIT license.

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

package net.edudb.page;

import java.util.ArrayList;

/**
 * A structure that is composed of multiple pages. E.g. database tables.
 * 
 * @author Ahmed Abdul Badie
 *
 */
public interface Pageable {

	/**
	 * 
	 * @return ArrayList<String> of page names holding the data.
	 */
	public ArrayList<String> getPageNames();

	/**
	 * Adds a new page name to the page names list.
	 * 
	 * @param pageName
	 *            Name of the page to add.
	 */
	public void addPageName(String pageName);
	
	/**
	 * Deletes the pages from disk.
	 */
	public void deletePages();

}
