package net.edudb.table;

public class TableWriterFactory extends TableAbstractFactory {

	@Override
	public TableReader getReader(TableFileType fileType) {
		return null;
	}

	@Override
	public TableWriter getWriter(TableFileType fileType) {
		switch (fileType) {
		case Binary:
			return new BinaryTableWriter();
		default:
			return null;
		}
	}

}
