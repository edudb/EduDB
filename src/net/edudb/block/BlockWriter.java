package net.edudb.block;

import java.io.IOException;

import net.edudb.page.Pageable;

public interface BlockWriter {
	
	public void write(Pageable page) throws IOException;

}
