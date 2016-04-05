/*
EduDB is made available under the OSI-approved MIT license.

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

package net.edudb.page;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

import net.edudb.block.*;
import net.edudb.engine.Config;
import net.edudb.structure.DBRecord;

public class PageTest {

	private static String pageName;

	@Test
	public void initTest() {
		Page page = new BinaryPage();

		assertNotNull(page);
		assertNotNull(page.getName());
		assertEquals(page.capacity(), Config.pageSize());
		assertEquals(page.size(), 0);
	}

	@Test
	public void saveTest() {
		Page page = new BinaryPage();
		page.addRecord(new DBRecord());
		page.addRecord(new DBRecord());
		page.addRecord(new DBRecord());

		BlockAbstractFactory blockWriterFactory = new BlockWriterFactory();
		BlockWriter blockWriter = blockWriterFactory.getWriter(BlockFileType.Binary);
		try {
			blockWriter.write(page);
		} catch (IOException e) {
			e.printStackTrace();
		}

		pageName = page.getName();

		assertEquals(page.size(), 3);
		File file = new File(Config.pagesPath() + page.getName() + ".block");
		assertTrue(file.exists());
	}

	@Test
	public void readTest() {
		BlockAbstractFactory blockReaderFactory = new BlockReaderFactory();
		BlockReader blockReader = blockReaderFactory.getReader(BlockFileType.Binary);

		try {
			Page page = blockReader.read(pageName);

			assertNotNull(page);
			assertEquals(page.size(), 3);
		} catch (ClassNotFoundException | IOException e) {
			e.printStackTrace();
		}
	}

}
