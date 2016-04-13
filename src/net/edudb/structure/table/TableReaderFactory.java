package net.edudb.structure.table;

public class TableReaderFactory extends TableAbstractFactory {

	@Override
	public TableReader getReader(TableFileType fileType) {
		switch (fileType) {
		case Binary:
			return new BinaryTableReader();
		default:
			return null;
		}
	}

	@Override
	public TableWriter getWriter(TableFileType fileType) {
		return null;
	}

}
