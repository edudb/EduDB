/*
EduDB is made available under the OSI-approved MIT license.

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

package net.edudb.table;

import java.io.Serializable;
import java.util.Collection;

import net.edudb.page.PageManager;
import net.edudb.page.Pageable;

public class BinaryTable implements Table, Serializable {

	private static final long serialVersionUID = -7002722265136341328L;

	/**
	 * Name of the table.
	 */
	private String name;

	/**
	 * Name of the pages in which data are persisted in.
	 */
//	private ArrayList<String> pages;
	private PageManager pageManager;

	public BinaryTable(String name) {
		this.name = name;
//		this.pages = new ArrayList<>();
		this.pageManager = new PageManager();
	}
	
	@Override
	public PageManager getPageManager() {
		return pageManager;
	}

	@Override
	public String getName() {
		return name;
	}

}
