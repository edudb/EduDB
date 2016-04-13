package net.edudb.structure.table;

public class TableFactory {
	public Table makeTable(TableFileType fileType, String tableName) {
		switch (fileType) {
		case Binary:
			return new BinaryTable(tableName);
		default:
			return null;
		}
	}
}
