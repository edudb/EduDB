/*
EduDB is made available under the OSI-approved MIT license.

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

package net.edudb.engine;

import java.io.IOException;
import java.util.HashMap;
import net.edudb.block.BlockAbstractFactory;
import net.edudb.block.BlockReader;
import net.edudb.block.BlockReaderFactory;
import net.edudb.block.BlockWriter;
import net.edudb.block.BlockWriterFactory;
import net.edudb.page.Page;
import net.edudb.server.ServerWriter;

/**
 * Singleton that handles pages read/written from/to disk.
 * 
 * @author Ahmed Abdul Badie
 *
 */
public class BufferManager {

	private static BufferManager instance = new BufferManager();
	HashMap<String, Page> pageBuffer;

	private BufferManager() {
		pageBuffer = new HashMap<String, Page>();
	}

	public static BufferManager getInstance() {
		return instance;
	}

	/**
	 * Reads a page from the disk and adds it to the buffer if not present in
	 * the buffer.
	 * 
	 * @param pageName
	 *            Name of page required.
	 * @return Page The read page.
	 */
	public synchronized Page read(String pageName) {

		Page page = null;

		page = pageBuffer.get(pageName);

		if (page != null) {
//			ServerWriter.getInstance().writeln("BufferManager (read): " + "Available");
			return page;
		}

//		ServerWriter.getInstance().writeln("BufferManager (read): " + "Not Available");

		page = this.readFromDisk(pageName);

		if (page != null) {
			pageBuffer.put(pageName, page);
		}
		return page;
	}

	/**
	 * Writes a page from the buffer to disk if available.
	 * 
	 * @param pageName
	 *            Name of page to write.
	 */
	public synchronized void write(String pageName) {
		Page page = null;

		page = pageBuffer.get(pageName);

		if (page != null) {
			this.writeToDisk(page);
		}
	}

	/**
	 * Writes a page to disk and adds it to the buffer.
	 * 
	 * @param page
	 *            The page to write.
	 */
	public synchronized void write(Page page) {
		this.pageBuffer.put(page.getName(), page);
//		this.writeToDisk(page);
	}

	/**
	 * Writes all the pages to disk.
	 */
	public void writeAll() {
		for (Page page : pageBuffer.values()) {
			this.write(page.getName());
		}
	}

	private Page readFromDisk(String pageName) {
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

	private void writeToDisk(Page page) {
		BlockAbstractFactory blockFactory = new BlockWriterFactory();
		BlockWriter blockWriter = blockFactory.getWriter(Config.blockType());

		try {
			blockWriter.write(page);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
