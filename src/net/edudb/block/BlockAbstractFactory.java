package net.edudb.block;

public abstract class BlockAbstractFactory {
	
	public abstract BlockReader getReader(BlockFileType fileType);
	
	public abstract BlockWriter getWriter(BlockFileType fileType);

}
