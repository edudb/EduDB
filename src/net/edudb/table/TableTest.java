/*
EduDB is made available under the OSI-approved MIT license.

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

package net.edudb.table;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import org.junit.Test;

import net.edudb.engine.Config;
import net.edudb.page.PageManager;

public class TableTest {

	@Test
	public void initTest() {
		Table table = new BinaryTable("test");
		PageManager pageManager = table.getPageManager();
		pageManager.addPageName("1");
		pageManager.addPageName("2");
		pageManager.addPageName("3");

		assertNotNull(table);
		assertEquals(table.getName(), "test");
		assertEquals(pageManager.getPageNames(), Arrays.asList("1", "2", "3"));
	}

	@Test
	public void saveTest() {
		Table table = new BinaryTable("test");
		PageManager pageManager = table.getPageManager();
		pageManager.addPageName("1");
		pageManager.addPageName("2");
		pageManager.addPageName("3");

		TableAbstractFactory tableWriterFactory = new TableWriterFactory();
		TableWriter tableWriter = tableWriterFactory.getWriter(TableFileType.Binary);
		try {
			tableWriter.write(table);
		} catch (IOException e) {
			e.printStackTrace();
		}

		File file = new File(Config.tablesPath() + table.getName() + ".table");
		assertTrue(file.exists());
	}

	@Test
	public void loadTest() {
		TableAbstractFactory tableReaderFactory = new TableReaderFactory();
		TableReader tableReader = tableReaderFactory.getReader(TableFileType.Binary);
		try {
			BinaryTable table = (BinaryTable) tableReader.read("test");

			assertNotNull(table);
			assertEquals(table.getName(), "test");
			assertEquals(table.getPageManager().getPageNames(), Arrays.asList("1", "2", "3"));
		} catch (ClassNotFoundException | IOException e) {
			e.printStackTrace();
		}

	}

}
