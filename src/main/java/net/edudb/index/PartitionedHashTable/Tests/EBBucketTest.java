package net.edudb.index.PartitionedHashTable.Tests;

import org.junit.Test;

import net.edudb.index.PartitionedHashTable.EBBucket;
import net.edudb.index.PartitionedHashTable.EBIndex;

import static org.junit.Assert.*;

import java.util.ArrayList;

/**
 * 
 * @author Ahmed Abdul Badie
 *
 */

public class EBBucketTest {
	
	@Test
	public void initTest() {
		EBBucket bucket = new EBBucket(5);

		assertEquals(5, bucket.capacity());
	}

	@Test
	public void addIndexTest() {

		EBBucket bucket = new EBBucket(5);
		String[] values = { "a", "b" };
		EBIndex index = new EBIndex(values, "page", 0);

		bucket.addIndex(index);

		assertEquals(1, bucket.size());
	}

	@Test
	public void updateIndexTest() {
		EBBucket bucket = new EBBucket(5);
		EBIndex index = new EBIndex(new String[] { "a", "b" }, "page", 0);
		bucket.addIndex(index);

		bucket.updateIndex(index, new EBIndex(new String[] { "a", "c" }, "test", 0));

		ArrayList<EBIndex> indexes = bucket.getIndex(index);

		assertArrayEquals(new String[] { "a", "c" }, indexes.get(0).getValues());
	}

	@Test
	public void getIndexTest() {
		EBBucket bucket = new EBBucket(5);
		EBIndex index = new EBIndex(new String[] { "a", "b" }, "page", 0);
		bucket.addIndex(index);

		ArrayList<EBIndex> indexes = bucket.getIndex(index);

		assertEquals(1, indexes.size());
	}
	
	@Test
	public void deleteIndexTest() {
		EBBucket bucket = new EBBucket(5);
		EBIndex index = new EBIndex(new String[] { "a", "b" }, "page", 0);
		bucket.addIndex(index);

		bucket.deleteIndex(index);

		assertEquals(0, bucket.size());
	}
}
