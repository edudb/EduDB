/*
EduDB is made available under the OSI-approved MIT license.

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

package net.edudb.engine;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import net.edudb.block.BlockFileType;
import net.edudb.structure.table.TableFileType;

/**
 * Stores the system's configuration.
 * 
 * @author Ahmed Abdul Badie
 *
 */
public class Config {

	/**
	 * 
	 * @return The type of the block file to save to disk.
	 */
	public static BlockFileType blockType() {
		return BlockFileType.Binary;
	}

	/**
	 * 
	 * @return The type of the table file to save to disk.
	 */
	public static TableFileType tableType() {
		return TableFileType.Binary;
	}

	/**
	 * 
	 * @return The system's absolute path on disk.
	 */
	public static String absolutePath() {
		try {
			return URLDecoder.decode(ClassLoader.getSystemClassLoader().getResource(".").getPath(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 
	 * @return The path to the current open database. Null if no database is
	 *         currently open.
	 */
	public static String databasePath() {
		return absolutePath() + "databases" + "/" + DatabaseSystem.getInstance().getDatabaseName();
	}

	/**
	 * 
	 * @return The path to the table files on disk.
	 */
	public static String tablesPath() {
		return absolutePath() + "databases" + "/" + DatabaseSystem.getInstance().getDatabaseName() + "/tables/";
	}

	/**
	 * 
	 * @return The path to the page files on disk.
	 */
	public static String pagesPath() {
		return absolutePath() + "databases" + "/" + DatabaseSystem.getInstance().getDatabaseName() + "/blocks/";
	}

	/**
	 * 
	 * @return The maximum allowed number of records inside a page.
	 */
	public static int pageSize() {
		return 100;
	}

	/**
	 * 
	 * @return The maximum allowed number of pages in the page buffer pool.
	 */
	public static int bufferSize() {
		return 1;
	}

}
