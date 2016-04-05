package net.edudb.block;

import java.io.IOException;

import net.edudb.page.Page;

public interface BlockReader {

	public Page read(String blockName) throws IOException, ClassNotFoundException;
	
}
