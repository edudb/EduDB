package net.edudb.page;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

import net.edudb.block.*;
import net.edudb.engine.Config;
import net.edudb.structure.DBRecord;

public class PageTest {
	
	private static String pageName;
	
	@Test
	public void initTest() {
		Page page = new BinaryPage();
		
		assertNotNull(page);
		assertNotNull(page.getName());
		assertEquals(page.capacity(), Config.pageSize());
		assertEquals(page.size(), 0);
	}
	
	@Test
	public void saveTest() {
		Page page = new BinaryPage();
		page.addRecord(new DBRecord());
		page.addRecord(new DBRecord());
		page.addRecord(new DBRecord());
		
		BlockAbstractFactory blockWriterFactory = new BlockWriterFactory();
		BlockWriter blockWriter = blockWriterFactory.getWriter(BlockFileType.Binary);
		try {
			blockWriter.write(page);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		pageName = page.getName();
		
		assertEquals(page.size(), 3);
		File file = new File(Config.pagesPath() + page.getName() + ".block");
		assertTrue(file.exists());
	}
	
	@Test
	public void readTest() {
		BlockAbstractFactory blockReaderFactory = new BlockReaderFactory();
		BlockReader blockReader = blockReaderFactory.getReader(BlockFileType.Binary);
		
		try {
			Page page = blockReader.read(pageName);
			
			assertNotNull(page);
			assertEquals(page.size(), 3);
		} catch (ClassNotFoundException | IOException e) {
			e.printStackTrace();
		}
	}

}
