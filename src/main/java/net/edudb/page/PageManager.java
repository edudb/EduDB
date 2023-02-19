/*
EduDB is made available under the OSI-approved MIT license.

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

package net.edudb.page;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;

import net.edudb.buffer.BufferManager;
import net.edudb.engine.Config;
import net.edudb.structure.Record;

/**
 * A structure that manages pages.
 * 
 * @author Ahmed Abdul Badie
 *
 */
public class PageManager implements Pageable, Serializable {

	private static final long serialVersionUID = -6801103344946561955L;

	/**
	 * List of page names the manager is responsible for.
	 */
	private ArrayList<String> pageNames;

	public PageManager() {
		this.pageNames = new ArrayList<>();
	}

	public PageManager(ArrayList<String> pages) {
		this.pageNames = pages;
	}

	@Override
	public ArrayList<String> getPageNames() {
		return pageNames;
	}

	@Override
	public void addPageName(String pageName) {
		this.pageNames.add(pageName);
	}

	@Override
	public void deletePages() {
		for (String pageName : pageNames) {
			File page = new File(Config.tablesPath() + pageName + ".block");
			if (page.exists()) {
				page.delete();
			}
		}

		pageNames.clear();
	}

	private synchronized Page createPage() {
		PageFactory pageFactory = new PageFactory();
		Page page = pageFactory.makePage(Config.blockType());
		pageNames.add(page.getName());

		return page;
	}

	/**
	 * Adds a record to the last page.
	 * 
	 * @param record
	 *            Record to be added to a page.
	 */
	public synchronized void addRecord(Record record) {
		Page page = null;
		if (pageNames.size() == 0) {
			Page newPage = createPage();
			BufferManager.getInstance().write(newPage);
		}

		page = BufferManager.getInstance().read(pageNames.get(pageNames.size() - 1));

		page.open();
		page.addRecord(record);
		if (page.isFull()) {
			Page newPage = createPage();
			BufferManager.getInstance().write(newPage);
		}
		page.close();
		// BufferManager.getInstance().write(page);
	}

	public void print() {
		for (String pageName : pageNames) {
			Page page = BufferManager.getInstance().read(pageName);
			page.print();
		}
	}

}
