package net.edudb.block;

import java.io.IOException;

import net.edudb.page.Page;

public interface BlockWriter {
	
	public void write(Page page) throws IOException;

}
