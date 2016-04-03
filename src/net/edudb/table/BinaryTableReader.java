package net.edudb.table;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

import net.edudb.engine.Config;

public class BinaryTableReader implements TableReader {

	@Override
	public Tabular read(String tableName) throws IOException, ClassNotFoundException {
		FileInputStream fileIn = new FileInputStream(Config.tablesPath() + tableName + ".table");
		ObjectInputStream in = new ObjectInputStream(fileIn);
		Tabular table = (Tabular) in.readObject();
		in.close();
		fileIn.close();
		return table;
	}

}
