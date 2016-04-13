/*
EduDB is made available under the OSI-approved MIT license.

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

package net.edudb.engine;

import java.io.File;
import java.io.IOException;

import net.edudb.structure.table.TableManager;

public class DatabaseSystem {

	private static DatabaseSystem instance = new DatabaseSystem();

	private DatabaseSystem() {
	}

	public static DatabaseSystem getInstance() {
		return instance;
	}

	/**
	 * Creates the required directories for the system to work properly.
	 */
	public void initializeDirectories() {
		createDatabasesDirectory();
		createTablesDirectory();
		createBlocksDirectory();
		createIndexesDirectory();
		createSchemaFile();
	}

	public void createDatabasesDirectory() {
		File databases = new File("database");
		if (!databases.exists()) {
			databases.mkdir();
		}
	}

	public void createTablesDirectory() {
		File tables = new File("database/tables");
		if (!tables.exists()) {
			tables.mkdir();
		}
	}

	public void createBlocksDirectory() {
		File blocks = new File("database/blocks");
		if (!blocks.exists()) {
			blocks.mkdir();
		}
	}

	public void createIndexesDirectory() {
		File indexes = new File("database/indexes");
		if (!indexes.exists()) {
			indexes.mkdir();
		}
	}
	
	public void createSchemaFile() {
		File schema = new File("database/schema.txt");
		if (!schema.exists()) {
			try {
				schema.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void exit(int status) {
		BufferManager.getInstance().writeAll();
		TableManager.getInstance().writeAll();
		System.exit(status);
	}

}
