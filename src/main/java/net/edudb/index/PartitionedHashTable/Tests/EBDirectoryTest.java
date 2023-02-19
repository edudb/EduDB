package net.edudb.index.PartitionedHashTable.Tests;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;

import net.edudb.index.PartitionedHashTable.EBDirectory;
import net.edudb.index.PartitionedHashTable.EBIndex;

/**
 * 
 * @author Ahmed Abdul Badie
 *
 */

public class EBDirectoryTest {
	
	@Test
	public void initTest() {
		EBDirectory directory = new EBDirectory(16, 2, 5);
		
		assertEquals(16, directory.length());
	}
	
	@Test
	public void addIndexTest() {
		EBDirectory directory = new EBDirectory(16, 2, 5);
		EBIndex index = new EBIndex(new String[] { "a", "b" }, "page", 0);
		directory.addIndex(index);
		
		ArrayList<EBIndex> indexes = directory.getIndex(index);
		
		assertEquals(1, indexes.size());
	}
	
	@Test
	public void updateIndexTest() {
		EBDirectory directory = new EBDirectory(16, 2, 5);
		EBIndex oldIndex = new EBIndex(new String[] { "a", "c" }, "page", 0);
		EBIndex newIndex = new EBIndex(new String[] { "d", null }, "page", 0);
		
		
		directory.addIndex(oldIndex);
		directory.updateIndex(oldIndex, newIndex);
		
		ArrayList<EBIndex> indexes = directory.getIndex(oldIndex);
		
//		System.out.println(indexes.get(0));
		
		assertArrayEquals(new String[] {"d", "c"}, indexes.get(0).getValues());
	}
	
	@Test
	public void getIndexTest() {
		EBDirectory directory = new EBDirectory(16, 2, 5);
		EBIndex index = new EBIndex(new String[] { "a", "b" }, "page", 0);
		
		
		directory.addIndex(index);
		
		ArrayList<EBIndex> indexes = directory.getIndex(index);
		
		assertEquals(index, indexes.get(0));
	}
	
	@Test
	public void getIndexesTest() {
		EBDirectory directory = new EBDirectory(16, 2, 5);
		for (int i = 0; i < 14; i++) {
			directory.addIndex(new EBIndex(new String[] {"a", i+""}, "page", i));
		}
		
		assertEquals(14, directory.getIndex(new EBIndex(new String[] {"a", null})).size());
	}
	
	@Test
	public void deleteIndexTest() {
		EBDirectory directory = new EBDirectory(16, 2, 5);
		EBIndex index = new EBIndex(new String[] { "a", "b" }, "page", 0);
		directory.addIndex(index);
		
		directory.deleteIndex(index);
		
		ArrayList<EBIndex> indexes = directory.getIndex(index);
		
		assertEquals(0, indexes.size());
	}

}
