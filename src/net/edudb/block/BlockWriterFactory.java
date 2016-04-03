package net.edudb.block;

public class BlockWriterFactory extends BlockAbstractFactory {

	@Override
	public BlockReader getReader(BlockFileType fileType) {
		return null;
	}

	@Override
	public BlockWriter getWriter(BlockFileType fileType) {
		switch (fileType) {
		case Binary:
			return new BinaryBlockWriter();
		case CSV:
			return new CSVBlockWriter();
		default:
			return null;
		}
	}

}
