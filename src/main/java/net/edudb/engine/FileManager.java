/*
EduDB is made available under the OSI-approved MIT license.

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

package net.edudb.engine;

import java.io.*;
import java.util.ArrayList;
import net.edudb.block.*;
import net.edudb.page.Page;
import net.edudb.structure.table.*;

public class FileManager {
	private static FileManager instance = new FileManager();
	private static String dataDirectory;
	private static String schema;
	private static boolean initialized;
	private static boolean isWindows;

	private FileManager() {
	}

	public static FileManager getInstance() {
		return instance;
	}

	public static ArrayList<String> readFile(String file) {
		if (!initialized) {
			init();
		}
		ArrayList<String> lines = new ArrayList<>();
		try {
			File dataFile = new File(file);
			if (!dataFile.exists()) {
				dataFile.createNewFile();
			}
			BufferedReader br = new BufferedReader(new FileReader(file));
			String line = br.readLine();
			while (line != null) {
				lines.add(line);
				line = br.readLine();
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return lines;
	}

	public static void init() {
		if (initialized) {
			return;
		}
		// dataDirectory = appendToPath(Config.absolutePath(), "database");
		dataDirectory = Config.databasePath();
		// File file = new File(dataDirectory);
		// if (!file.exists()) {
		// file.mkdir();
		// }
		String operatingSystem = System.getProperty("os.name").toLowerCase();
		if (operatingSystem.startsWith("windows")) {
			isWindows = true;
		} else {
			isWindows = false;
		}
		schema = appendToPath(dataDirectory, "schema.txt");
		initialized = true;
	}

	public static String getSchema() {
		init();
		return appendToPath(Config.databasePath(), "schema.txt");
	}

	private static String appendToPath(String S1, String S2) {
		if (isWindows) {
			return S1 + "\\" + S2;
		} else {
			return S1 + "/" + S2;
		}
	}

	public static void addToFile(String file, String text) {
		try {
			File dataFile = new File(file);
			if (!dataFile.exists()) {
				dataFile.createNewFile();
			}
			BufferedWriter output = new BufferedWriter(new FileWriter(file, true));
			output.append(text);
			output.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void createTable(String tableName) {
		try {
			String dir = appendToPath(dataDirectory, tableName);
			File dataFile = new File(dir);
			if (!dataFile.exists()) {
				dataFile.mkdir();
				File table = new File(appendToPath(dir, (tableName + ".txt")));
				table.createNewFile();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static String getTable(String tableName) {
		String table;
		try {
			String dir = appendToPath(dataDirectory, tableName);
			table = appendToPath(dir, (tableName + ".txt"));
			return table;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void writeToFile(String data, String fileName) {
		try {
			File dataFile = new File(fileName);
			dataFile.delete();
			dataFile.createNewFile();
			BufferedWriter output = new BufferedWriter(new FileWriter(fileName, true));
			output.write(data);
			output.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Reads a page from disk using a block reader.
	 * 
	 * @param pageName
	 *            The name of the page to read.
	 * @return The read page.
	 */
	public Page readPage(String pageName) {
		BlockAbstractFactory blockFactory = new BlockReaderFactory();
		BlockReader blockReader = blockFactory.getReader(Config.blockType());

		try {
			Page page = blockReader.read(pageName);
			return page;
		} catch (ClassNotFoundException | IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Writes a page to disk using a block writer.
	 * 
	 * @param page
	 *            The page to write.
	 */
	public void writePage(Page page) {
		BlockAbstractFactory blockFactory = new BlockWriterFactory();
		BlockWriter blockWriter = blockFactory.getWriter(Config.blockType());

		try {
			blockWriter.write(page);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Reads a table from disk using a table reader.
	 * 
	 * @param tableName
	 *            The name of the table to read.
	 * @return The read table.
	 */
	public Table readTable(String tableName) {
		TableAbstractFactory tableFactory = new TableReaderFactory();
		TableReader tableReader = tableFactory.getReader(Config.tableType());

		try {
			Table table = tableReader.read(tableName);
			return table;
		} catch (ClassNotFoundException | IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Writes a table to disk using a table writer.
	 * 
	 * @param table
	 *            The table to write.
	 */
	public void writeTable(Table table) {
		TableAbstractFactory tableFactory = new TableWriterFactory();
		TableWriter blockWriter = tableFactory.getWriter(Config.tableType());

		try {
			blockWriter.write(table);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
