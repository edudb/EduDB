package net.edudb.engine;

import java.io.File;

public class DatabaseSystem {
	
	private static DatabaseSystem instance = new DatabaseSystem();
	
	private DatabaseSystem() {
	}
	
	public static DatabaseSystem getInstance(){
		return instance;
	}
	
	public void initialize() {
		createDatabasesDirectory();
		createTablesDirectory();
		createBlocksDirectory();
		createIndexesDirectory();
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
	
}
