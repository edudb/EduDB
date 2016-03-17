package net.edudb.index.PartitionedHashTable.Tests;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;

import net.edudb.index.PartitionedHashTable.EBBucketList;
import net.edudb.index.PartitionedHashTable.EBIndex;

/**
 * 
 * @author Ahmed Abdul Badie
 *
 */

public class EBBucketListTest {

	@Test
	public void initTest() {
		EBBucketList bucketList = new EBBucketList(5);

		assertEquals(1, bucketList.buckets());
	}

	@Test
	public void addIndexTest() {
		EBBucketList bucketList = new EBBucketList(5);
		EBIndex index = new EBIndex(new String[] { "a", "b" }, "page", 0);

		bucketList.addIndex(index);

		assertEquals(1, bucketList.getIndex(index).size());

		ArrayList<EBIndex> indexes = bucketList.getIndex(index);

		assertEquals(1, indexes.size());
	}

	@Test
	public void updateIndexTest() {
		EBBucketList bucketList = new EBBucketList(5);
		EBIndex oldIndex = new EBIndex(new String[] { "a", "b" }, "page", 0);
		EBIndex newIndex = new EBIndex(new String[] { "c", "d" }, "page", 0);

		bucketList.addIndex(oldIndex);
		bucketList.updateIndex(oldIndex, newIndex);
		
		ArrayList<EBIndex> indexes = bucketList.getIndex(newIndex);
		
		assertArrayEquals(newIndex.getValues(), indexes.get(0).getValues());
	}
	
	@Test
	public void getIndexTest() {
		EBBucketList bucketList = new EBBucketList(5);
		EBIndex index = new EBIndex(new String[] { "a", "b" }, "page", 0);

		bucketList.addIndex(index);

		ArrayList<EBIndex> indexes = bucketList.getIndex(index);

		assertEquals(index, indexes.get(0));
	}
	
	@Test
	public void getIndexesTest() {
		EBBucketList bucketList = new EBBucketList(5);
		for (int i = 0; i < 7; i++) {
			bucketList.addIndex(new EBIndex(new String[] {"a", "b"}, "page", i));
		}
		
		ArrayList<EBIndex> indexes = bucketList.getIndex(new EBIndex(new String[] {"a", "b"}));
		
		assertEquals(7, indexes.size());
	}
	
	@Test
	public void deleteIndex() {
		EBBucketList bucketList = new EBBucketList(5);
		EBIndex index = new EBIndex(new String[] { "a", "b" }, "page", 0);

		bucketList.addIndex(index);

		bucketList.deleteIndex(index);
		
		ArrayList<EBIndex> indexes = bucketList.getIndex(index);

		assertEquals(0, indexes.size());
	}
	
	@Test
	public void deleteIndexesTest() {
		EBBucketList bucketList = new EBBucketList(5);
		for (int i = 0; i < 7; i++) {
			bucketList.addIndex(new EBIndex(new String[] {"a", "b"}, "page", i));
		}
		
		bucketList.deleteIndex(new EBIndex(new String[] {"a", "b"}));
		
		ArrayList<EBIndex> indexes = bucketList.getIndex(new EBIndex(new String[] {"a", "b"}));
		
		assertEquals(0, indexes.size());
	}
	
	@Test
	public void overflowTest() {
		EBBucketList bucketList = new EBBucketList(5);
		for (int i = 0; i < 7; i++) {
			bucketList.addIndex(new EBIndex(new String[] {"a", "b"}, "page", i));
		}
		
		assertEquals(2, bucketList.buckets());
	}

}
