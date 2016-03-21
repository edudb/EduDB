package net.edudb.block;

public abstract class BlockAbstractFactory {
	
	public abstract BlockReader getReader();
	
	public abstract BlockWriter getWrtier();

}
