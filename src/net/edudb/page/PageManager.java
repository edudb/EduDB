/*
EduDB is made available under the OSI-approved MIT license.

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

package net.edudb.page;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

import net.edudb.block.BlockAbstractFactory;
import net.edudb.block.BlockReader;
import net.edudb.block.BlockReaderFactory;
import net.edudb.block.BlockWriter;
import net.edudb.block.BlockWriterFactory;
import net.edudb.engine.BufferManager;
import net.edudb.engine.Config;
import net.edudb.structure.Recordable;

/**
 * 
 * @author Ahmed Abdul Badie
 *
 */
public class PageManager implements Pageable, Serializable {

	/**
	 * 
	 */
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

	/**
	 * Handles reading a page directly from disk.
	 * 
	 * @param page
	 *            Name of page to read.
	 */
	public static Page read(String pageName) {
		Page page = null;
		BlockAbstractFactory blockReaderFactory = new BlockReaderFactory();
		BlockReader blockReader = blockReaderFactory.getReader(Config.blockType());
		try {
			page = blockReader.read(pageName);
			return page;
		} catch (ClassNotFoundException | IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Handles writing a page directly to disk.
	 * 
	 * @param page
	 *            Page to be written.
	 */
	public static void write(Page page) {
		BlockAbstractFactory blockFactory = new BlockWriterFactory();
		BlockWriter blockWriter = blockFactory.getWriter(Config.blockType());

		try {
			blockWriter.write(page);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public Collection<String> getPageNames() {
		return pageNames;
	}

	@Override
	public void addPageName(String pageName) {
		this.pageNames.add(pageName);
	}

	private synchronized Page createPage() {
		PageFactory pageFactory = new PageFactory();
		Page page = pageFactory.makePage(Config.blockType());
		pageNames.add(page.getName());

		return page;
	}

	/**
	 * 
	 * 
	 * @param record
	 *            Record to be added to a page.
	 */
	public synchronized void addRecord(Recordable record) {
		Page page = null;
		if (pageNames.size() == 0) {
			Page newPage = createPage();
			/**
			 * No need to access the Buffer Manager since the page is not yet
			 * requested by the engine.
			 */
			PageManager.write(newPage);
		}

		page = BufferManager.getInstance().read(pageNames.get(pageNames.size() - 1));

		page.open();
		page.addRecord(record);
		if (page.isFull()) {
			Page newPage = createPage();
			PageManager.write(newPage);
		}
		page.close();
//		PageManager.write(page);
	}

	public void print() {
		for (String pageName : pageNames) {
			Page page = BufferManager.getInstance().read(pageName);
			page.print();
		}
	}

}
