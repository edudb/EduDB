/*
EduDB is made available under the OSI-approved MIT license.

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

package net.edudb.buffer;

import java.util.LinkedHashMap;
import net.edudb.engine.FileManager;
import net.edudb.page.Page;

/**
 * Singleton that handles pages read/written from/to disk.
 * 
 * @author Ahmed Abdul Badie
 *
 */
public class BufferManager {

	private static BufferManager instance = new BufferManager();
	/**
	 * The buffer pool.
	 */
	LinkedHashMap<String, Page> pageBuffer;
	/**
	 * The replacement algorithm.
	 */
	private PageReplacement replacement;

	private BufferManager() {
		pageBuffer = new LinkedHashMap<String, Page>();
		this.replacement = new LRUPageReplacement(pageBuffer);
	}

	public static BufferManager getInstance() {
		return instance;
	}

	/**
	 * Reads a page from the disk and adds it to the buffer if not present in
	 * the buffer. If the page is inside the buffer pool, it is directly
	 * returned.
	 * 
	 * @param pageName
	 *            Name of page to read.
	 * @return Page The read page.
	 */
	public synchronized Page read(String pageName) {

		Page page = replacement.read(pageName);

		// page = pageBuffer.get(pageName);
		if (page != null) {
			// pageBuffer.put(page.getName(),
			// pageBuffer.remove(page.getName()));
			return page;
		}

		page = this.readFromDisk(pageName);

		if (page != null) {
			// pageBuffer.put(pageName, page);
			replacement.put(page);
		}
		return page;
	}

	// /**
	// * Writes a page from the buffer to disk if available.
	// *
	// * @param pageName
	// * Name of page to write.
	// */
	// public synchronized void write(String pageName) {
	// Page page = null;
	//
	// page = pageBuffer.get(pageName);
	//
	// if (page != null) {
	// this.writeToDisk(page);
	// }
	// }

	/**
	 * Writes a page to disk and adds it to the buffer.
	 * 
	 * @param page
	 *            The page to write.
	 */
	public synchronized void write(Page page) {
		// this.pageBuffer.put(page.getName(), page);
		replacement.put(page);
	}

	private synchronized void remove() {
		replacement.remove();
	}

	/**
	 * Writes all the pages to disk. Used when closing the database for the data
	 * to be persisted.
	 */
	public void writeAll() {
		for (Page page : pageBuffer.values()) {
			this.writeToDisk(page);
		}
	}

	/**
	 * Reads a page from disk by requesting the read from the
	 * {@link FileManager}.
	 * 
	 * @param pageName
	 *            The name of the page to be read from disk.
	 * @return The page read from disk.
	 */
	private Page readFromDisk(String pageName) {
		// Page page = null;
		// BlockAbstractFactory blockReaderFactory = new BlockReaderFactory();
		// BlockReader blockReader =
		// blockReaderFactory.getReader(Config.blockType());
		// try {
		// page = blockReader.read(pageName);
		// return page;
		// } catch (ClassNotFoundException | IOException e) {
		// e.printStackTrace();
		// }
		//
		// return null;

		return FileManager.getInstance().readPage(pageName);
	}

	/**
	 * Writes a page to disk by requesting the write from the
	 * {@link FileManager}.
	 * 
	 * @param page
	 *            The page to be written to disk.
	 */
	private void writeToDisk(Page page) {
		// BlockAbstractFactory blockFactory = new BlockWriterFactory();
		// BlockWriter blockWriter = blockFactory.getWriter(Config.blockType());
		//
		// try {
		// blockWriter.write(page);
		// } catch (IOException e) {
		// e.printStackTrace();
		// }
		FileManager.getInstance().writePage(page);
	}

}
