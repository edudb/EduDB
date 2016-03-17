package net.edudb.index.PartitionedHashTable.Tests;

import static org.junit.Assert.*;

import org.junit.Test;

import net.edudb.index.PartitionedHashTable.EBIndex;


/**
 * 
 * @author Ahmed Abdul Badie
 *
 */

public class EBIndexTest {

	@Test
	public void initTest() {
		EBIndex index = new EBIndex(new String[] { "a", "b" }, "test", 12);

		assertArrayEquals(new String[] { "a", "b" }, index.getValues());
		assertEquals("test", index.getPageName());
		assertEquals(12, index.getRowNumber());
		assertEquals(false, index.isDeleted());
	}

	@Test
	public void shortInitTest() {
		EBIndex index = new EBIndex(new String[] { "a", "b" });

		assertArrayEquals(new String[] { "a", "b" }, index.getValues());
		assertEquals(null, index.getPageName());
		assertEquals(0, index.getRowNumber());
		assertEquals(false, index.isDeleted());
	}

	@Test
	public void setValuesTest() {
		EBIndex index = new EBIndex(new String[] { "a", "b" }, "test", 12);

		index.setValues(new String[] { "c", "d" });
		assertArrayEquals(new String[] { "c", "d" }, index.getValues());

		index.setValues(new String[] { null, "e" });
		assertArrayEquals(new String[] { "c", "e" }, index.getValues());

		index.setValues(new String[] { null, null });
		assertArrayEquals(new String[] { "c", "e" }, index.getValues());
	}

	@Test
	public void setDeletedTest() {
		EBIndex index = new EBIndex(new String[] { "a", "b" }, "test", 12);

		index.setDeleted(true);
		assertEquals(true, index.isDeleted());
	}

	@Test
	public void equalityTest() {
		EBIndex index1 = new EBIndex(new String[] { "a", "b" }, "test", 12);
		EBIndex index2 = new EBIndex(new String[] { "a", "b" }, "test", 12);

		assertEquals(index1, index2);

		EBIndex index3 = new EBIndex(new String[] { null, "b" }, "test", 12);

		assertEquals(index1, index3);
	}
}
