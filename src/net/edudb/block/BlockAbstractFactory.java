package net.edudb.block;

public abstract class BlockAbstractFactory {
	
	public abstract BlockReader getReader(BlockFileType blockType);
	
	public abstract BlockWriter getWriter(BlockFileType blockType);

}
