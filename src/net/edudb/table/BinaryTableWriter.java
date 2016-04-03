package net.edudb.table;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import net.edudb.engine.Config;

public class BinaryTableWriter implements TableWriter {

	@Override
	public void write(Tabular table) throws IOException {
		FileOutputStream fileOut = new FileOutputStream(Config.tablesPath() + table.getName() + ".table");
		ObjectOutputStream out = new ObjectOutputStream(fileOut);
		out.writeObject(table);
		out.close();
		fileOut.close();
	}

}
