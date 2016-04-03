package net.edudb.block;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

import net.edudb.engine.Config;
import net.edudb.page.Pageable;

public class BinaryBlockReader implements BlockReader {

	@Override
	public Pageable read(String blockName) throws IOException, ClassNotFoundException {
		FileInputStream fileIn = new FileInputStream(Config.pagesPath() + blockName + ".block");
		ObjectInputStream in = new ObjectInputStream(fileIn);
		Pageable page = (Pageable) in.readObject();
		in.close();
		fileIn.close();
		return page;
	}

}
