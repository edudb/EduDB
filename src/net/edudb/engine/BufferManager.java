package net.edudb.engine;

import java.io.IOException;
import java.util.HashMap;

import net.edudb.block.*;
import net.edudb.page.Pageable;
import net.edudb.server.ServerWriter;

public class BufferManager {

	private static BufferManager instance = new BufferManager();
	HashMap<String, Pageable> pages;

	private BufferManager() {
		pages = new HashMap<String, Pageable>();
	}

	public static BufferManager getInstance() {
		return instance;
	}

	public synchronized Pageable read(String pageName) {

		Pageable page = null;

		page = pages.get(pageName);

		if (page != null) {
			ServerWriter.getInstance().writeln("BufferManager (read): " + "Available");
			return page;
		}

		ServerWriter.getInstance().writeln("BufferManager (read): " + "Not Available");

		BlockAbstractFactory blockReaderFactory = new BlockReaderFactory();
		BlockReader blockReader = blockReaderFactory.getReader(BlockFileType.Binary);
		try {
			page = blockReader.read(pageName);
			pages.put(pageName, page);
			return page;
		} catch (ClassNotFoundException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public synchronized void write(String pageName) {
		Pageable page = null;

		page = pages.get(pageName);

		if (page != null) {
			BlockAbstractFactory blockFactory = new BlockWriterFactory();
			BlockWriter blockWriter = blockFactory.getWriter(BlockFileType.Binary);

			try {
				blockWriter.write(page);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
