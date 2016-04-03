package net.edudb.table;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import org.junit.Test;

import net.edudb.engine.Config;

public class TableTest {

	@Test
	public void initTest() {
		Tabular table = new Table("test");
		table.addPageName("1");
		table.addPageName("2");
		table.addPageName("3");
		
		assertNotNull(table);
		assertEquals(table.getName(), "test");
		assertEquals(table.getPageNames(), Arrays.asList("1", "2", "3"));
	}

	@Test
	public void saveTest() {
		Tabular table = new Table("test");
		table.addPageName("1");
		table.addPageName("2");
		table.addPageName("3");
		
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
			Table table = (Table) tableReader.read("test");
			
			assertNotNull(table);
			assertEquals(table.getName(), "test");
			assertEquals(table.getPageNames(), Arrays.asList("1", "2", "3"));
		} catch (ClassNotFoundException | IOException e) {
			e.printStackTrace();
		}
		
		
	}

}
