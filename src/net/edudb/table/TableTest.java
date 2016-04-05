package net.edudb.table;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import org.junit.Test;

import net.edudb.engine.Config;
import net.edudb.page.PageManager;

public class TableTest {

	@Test
	public void initTest() {
		Table table = new BinaryTable("test");
		PageManager pageManager = table.getPageManager();
		pageManager.addPageName("1");
		pageManager.addPageName("2");
		pageManager.addPageName("3");
		
		assertNotNull(table);
		assertEquals(table.getName(), "test");
		assertEquals(pageManager.getPageNames(), Arrays.asList("1", "2", "3"));
	}

	@Test
	public void saveTest() {
		Table table = new BinaryTable("test");
		PageManager pageManager = table.getPageManager();
		pageManager.addPageName("1");
		pageManager.addPageName("2");
		pageManager.addPageName("3");
		
		TableAbstractFactory tableWriterFactory = new TableWriterFactory();
		TableWriter tableWriter = tableWriterFactory.getWriter(TableFileType.Binary);
		try {
			tableWriter.write(table);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		File file = new File(Config.tablesPath() + table.getName() + ".table");
		assertTrue(file.exists());
	}
	
	@Test
	public void loadTest() {
		TableAbstractFactory tableReaderFactory = new TableReaderFactory();
		TableReader tableReader = tableReaderFactory.getReader(TableFileType.Binary);
		try {
			BinaryTable table = (BinaryTable) tableReader.read("test");
			
			assertNotNull(table);
			assertEquals(table.getName(), "test");
			assertEquals(table.getPageManager().getPageNames(), Arrays.asList("1", "2", "3"));
		} catch (ClassNotFoundException | IOException e) {
			e.printStackTrace();
		}
		
		
	}

}
