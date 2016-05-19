/*
EduDB is made available under the OSI-approved MIT license.

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

package net.edudb.engine;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

import net.edudb.buffer.BufferManager;
import net.edudb.server.ServerWriter;
import net.edudb.structure.table.TableManager;

/**
 * 
 * Manages the system's databases and their required files and directories.
 * 
 * @author Ahmed Abdul Badie
 *
 */
public class DatabaseSystem {

	private static DatabaseSystem instance = new DatabaseSystem();
	private String databasesString = "databases";
	private String databaseName;
	private boolean databaseIsOpen;

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
	}

	/**
	 * Initialized the required directories for the database to be able to
	 * function properly. These are the directories where files are saved to
	 * disk.
	 * 
	 * @param databaseName
	 *            The name of the database to initialize its directories.
	 */
	private void initializeDatabaseDirectories(String databaseName) {
		createTablesDirectory(databaseName);
		createBlocksDirectory(databaseName);
		/**
		 * Used to create index directory
		 */
		// createIndexesDirectory(databaseName);
		createSchemaFile(databaseName);
	}

	/**
	 * 
	 * @return The name of the current open database.
	 */
	public String getDatabaseName() {
		return this.databaseName;
	}

	/**
	 * 
	 * @return Whether the system has an open database.
	 */
	public boolean databaseIsOpen() {
		return this.databaseIsOpen;
	}

	/**
	 * Opens a given database if it is available.
	 * 
	 * @param databaseName
	 *            The name of the database to open.
	 */
	public void open(String databaseName) {
		if (databaseExists(databaseName)) {
			this.databaseName = databaseName;
			this.databaseIsOpen = true;
			initializeDatabaseDirectories(this.databaseName);
			ServerWriter.getInstance().writeln("Opened database '" + databaseName + "'");
		} else {
			ServerWriter.getInstance().writeln("Database '" + databaseName + "' does not exist");
		}
	}

	/**
	 * Closes the current open database, if any.
	 */
	public void close() {
		if (!databaseIsOpen) {
			ServerWriter.getInstance().writeln("No open database");
			return;
		}

		BufferManager.getInstance().writeAll();
		TableManager.getInstance().writeAll();

		String dbName = databaseName;

		this.databaseName = null;
		this.databaseIsOpen = false;

		ServerWriter.getInstance().writeln("Closed database '" + dbName + "'");
	}

	/**
	 * Creates a new database in the system iff it does not exist.
	 * 
	 * @param databaseName
	 *            The name of the database to create.
	 */
	public void createDatabase(String databaseName) {
		if (databaseExists(databaseName)) {
			ServerWriter.getInstance().writeln("Database '" + databaseName + "' does exist");
			return;
		}
		if (databaseIsOpen) {
			close();
		}
		new File(Config.absolutePath() + databasesString + "/" + databaseName).mkdir();
		ServerWriter.getInstance().writeln("Created database '" + databaseName + "'");
		open(databaseName);
	}

	/**
	 * Drops a database from the system iff it does exist.
	 * 
	 * @param databaseName
	 *            The name of the database to drop.
	 * @throws IOException
	 */
	public void dropDatabase(String databaseName) throws IOException {
		if (!databaseExists(databaseName)) {
			ServerWriter.getInstance().writeln("Database '" + databaseName + "' does not exist");
			return;
		}
		if (databaseIsOpen) {
			close();
		}

		Path directory = Paths.get(Config.absolutePath() + databasesString + "/" + databaseName);
		Files.walkFileTree(directory, new SimpleFileVisitor<Path>() {
			@Override
			public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
				Files.delete(file);
				return FileVisitResult.CONTINUE;
			}

			@Override
			public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
				Files.delete(dir);
				return FileVisitResult.CONTINUE;
			}
		});

		ServerWriter.getInstance().writeln("Dropped database '" + databaseName + "'");
	}

	/**
	 * Checks whether the given database exists in the system.
	 * 
	 * @param databaseName
	 *            The name of the database to check.
	 * @return The availability of the database.
	 */
	private boolean databaseExists(String databaseName) {
		return new File(Config.absolutePath() + databasesString + "/" + databaseName).exists();
	}

	/**
	 * Creates the database's root directory.
	 */
	private void createDatabasesDirectory() {
		File databases = new File(Config.absolutePath() + databasesString);
		if (!databases.exists()) {
			databases.mkdir();
		}
	}

	/**
	 * Creates the database's tables directory.
	 * 
	 * @param databaseName
	 *            The name of the database.
	 */
	private void createTablesDirectory(String databaseName) {
		File tables = new File(Config.absolutePath() + databasesString + "/" + databaseName + "/tables");
		if (!tables.exists()) {
			tables.mkdir();
		}
	}

	/**
	 * Creates the database's pages directory.
	 * 
	 * @param databaseName
	 *            The name of the database.
	 */
	private void createBlocksDirectory(String databaseName) {
		File blocks = new File(Config.absolutePath() + databasesString + "/" + databaseName + "/blocks");
		if (!blocks.exists()) {
			blocks.mkdir();
		}
	}

	/**
	 * This method is documented since the indx is not yet supported
	 */
	// private void createIndexesDirectory(String databaseName) {
	// File indexes = new File(Config.absolutePath() + databasesString + "/" +
	// databaseName + "/indexes");
	// if (!indexes.exists()) {
	// indexes.mkdir();
	// }
	// }

	/**
	 * Creates the database's schema file.
	 * 
	 * @param databaseName
	 *            The name of the database.
	 */
	private void createSchemaFile(String databaseName) {
		File schema = new File(Config.absolutePath() + databasesString + "/" + databaseName + "/schema.txt");
		if (!schema.exists()) {
			try {
				schema.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Exits the system.
	 * 
	 * @param status
	 *            The status of the exit.
	 */
	public void exit(int status) {
		if (databaseIsOpen) {
			close();
		}

		/**
		 * Writes exit to the client to close the connection.
		 */
		if (ServerWriter.getInstance().getContext() != null) {
			ServerWriter.getInstance().writeln("[edudb::exit]");
		}
		System.exit(status);
	}

}
