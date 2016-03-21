package net.edudb.block;

import net.edudb.page.Page;

public interface BlockReader {

	public Page read(String blockName);
	
}
