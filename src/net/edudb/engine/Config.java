package net.edudb.engine;

import net.edudb.block.BlockFileType;

public class Config {
	
	public static BlockFileType blockType() {
		return BlockFileType.CSV;
	}
	
	public static String databasesPath() {
		return "databases";
	}
	
	public static String tablesPath() {
		return "database/tables/";
	}
	
	public static String pagesPath() {
		return "database/blocks/";
	}
	
	public static int pageSize() {
		return 100;
	}

}
