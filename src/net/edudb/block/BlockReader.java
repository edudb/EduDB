package net.edudb.block;

import java.io.IOException;

import net.edudb.page.Pageable;

public interface BlockReader {

	public Pageable read(String blockName) throws IOException, ClassNotFoundException;
	
}
