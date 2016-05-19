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
 * Replaces pages from the buffer pool according to the LRU algorithm. The least
 * recently used page will be the first to be removed from the buffer. New pages
 * will be inserted at the end of the pool. Any requested page that is in the pool
 * will be moved to the end of it; this ensures that the least recently used
 * pages are at the beginning of the pool.
 * 
 * @author Ahmed Abdul Badie
 *
 */
public class LRUPageReplacement extends PageReplacement {

	public LRUPageReplacement(LinkedHashMap<String, Page> pageBuffer) {
		super(pageBuffer);
	}

	@Override
	public Page read(String pageName) {
		Page page = null;

		page = pageBuffer.get(pageName);

		if (page != null) {
			pageBuffer.put(page.getName(), pageBuffer.remove(page.getName()));
		}

		return page;
	}

	@Override
	public void put(Page page) {
		pageBuffer.put(page.getName(), page);
	}

	@Override
	public void remove() {
		String firstPageName = (String) pageBuffer.entrySet().iterator().next().getKey();
		pageBuffer.remove(firstPageName);
	}

}
