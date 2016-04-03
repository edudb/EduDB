package net.edudb.block;

import net.edudb.page.DBPage;
import net.edudb.page.Pageable;
import net.edudb.server.ServerWriter;

public class CSVBlockWriter implements BlockWriter {
	
	public void write(Pageable page) {
		ServerWriter.getInstance().writeln("Writing");
		((DBPage) page).write();
	}

}
