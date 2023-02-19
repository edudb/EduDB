/*
EduDB is made available under the OSI-approved MIT license.

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

package net.edudb.buffer;

import java.util.LinkedHashMap;

import net.edudb.page.Page;

/**
 * Handles the replacement of pages in the buffer pool.
 * 
 * @author Ahmed Abdul Badie
 *
 */
public abstract class PageReplacement {
	/**
	 * The buffer pool.
	 */
	LinkedHashMap<String, Page> pageBuffer;

	public PageReplacement(LinkedHashMap<String, Page> pageBuffer) {
		this.pageBuffer = pageBuffer;
	}

	/**
	 * Reads a page from the buffer pool.
	 * 
	 * @param pageName
	 *            Name of the page to read from the buffer pool.
	 * @return The read page.
	 */
	public abstract Page read(String pageName);

	/**
	 * 
	 * Insert a page into the buffer pool.
	 * 
	 * @param page
	 *            Page to insert into the buffer pool.
	 */
	public abstract void put(Page page);

	/**
	 * Removes a page from the buffer pool according to a page replacement
	 * algorithm. Subclasses of {@link PageReplacement} are responsible for
	 * handling how the page is removed.
	 */
	public abstract void remove();
}
