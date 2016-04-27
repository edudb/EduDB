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

public class Config {
	
	public static BlockFileType blockType() {
		return BlockFileType.Binary;
	}
	
	public static TableFileType tableType() {
		return TableFileType.Binary;
	}
	
	public static String absolutePath() {
		try {
			return URLDecoder.decode(ClassLoader.getSystemClassLoader().getResource(".").getPath(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static String databasesPath() {
		return absolutePath() + "databases";
	}
	
	public static String tablesPath() {
		return absolutePath() +  "database/tables/";
	}
	
	public static String pagesPath() {
		return absolutePath() + "database/blocks/";
	}
	
	public static int pageSize() {
		return 5;
	}

}
