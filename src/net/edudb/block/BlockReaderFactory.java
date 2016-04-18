package net.edudb.block;

public class BlockReaderFactory extends BlockAbstractFactory {

	@Override
	public BlockReader getReader(BlockFileType fileType) {
		switch (fileType) {
		case Binary:
			return new BinaryBlockReader();
		default:
			return null;
		}
	}

	@Override
	public BlockWriter getWriter(BlockFileType fileType) {
		// TODO Auto-generated method stub
		return null;
	}

}
