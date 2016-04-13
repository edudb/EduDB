/*
EduDB is made available under the OSI-approved MIT license.

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

package net.edudb.relation;

import java.util.ArrayList;
import java.util.Iterator;

import net.edudb.engine.BufferManager;
import net.edudb.page.Page;
import net.edudb.structure.Record;

public class RelationIterator implements Iterator<Record> {

	private ArrayList<String> pageNames;
	private Page currentPage;
	private int currentPageIndex;
	private int currentIndex;

	public RelationIterator(ArrayList<String> pageNames) {
		this.pageNames = pageNames;
		if (this.pageNames.size() > 0) {
			this.currentPage = BufferManager.getInstance().read(pageNames.get(currentPageIndex++));
			this.currentIndex = 0;
		}
	}

	@Override
	public boolean hasNext() {
		if (currentPage == null) {
			return false;
		}
		if (currentIndex > 0 && currentIndex >= currentPage.size()) {
			if (currentPageIndex < pageNames.size()) {
				currentPage = BufferManager.getInstance().read(pageNames.get(currentPageIndex++));
				this.currentIndex = 0;
			}
		}
		return currentIndex < currentPage.size();
	}

	@Override
	public Record next() {
		if (!hasNext()) {
			return null;
		}
		return currentPage.getRecord(currentIndex++);
	}

}
