package net.edudb.block;

import net.edudb.page.DBPage;
import net.edudb.page.Page;
import net.edudb.server.ServerWriter;

public class CSVBlockWriter implements BlockWriter {
	
	public void write(Page page) {
		ServerWriter.getInstance().writeln("Writing");
		((DBPage) page).write();
	}

}
