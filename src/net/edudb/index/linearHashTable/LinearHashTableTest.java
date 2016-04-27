/*
EduDB is made available under the OSI-approved MIT license.

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

package net.edudb.index.linearHashTable;

import static org.junit.Assert.*;

import org.junit.Test;
import net.edudb.data_type.DataType;
import net.edudb.data_type.IntegerType;
import net.edudb.data_type.VarCharType;

public class LinearHashTableTest {

	@Test
	public void testPutOneGetOne() {
		LinearHashTable table = new LinearHashTable(0.75f, 2);

		IntegerType key = new IntegerType(5);
		VarCharType value = new VarCharType("five");
		table.put(key, value);
		assertTrue(key.equals(key));
		assertEquals("get as put", value, table.get(key));
	}

	@Test
	public void testResize() {
		LinearHashTable table = new LinearHashTable(0.75f, 2);
		IntegerType key1 = new IntegerType(5);
		VarCharType value1 = new VarCharType("five");
		table.put(key1, value1);
		assertEquals("table size increased", 1, table.size());
		IntegerType key2 = new IntegerType(6);
		VarCharType value2 = new VarCharType("six");
		table.put(key2, value2);
		assertEquals("table size increased", 2, table.size());
	}

	@Test
	public void put100() {
		LinearHashTable table = new LinearHashTable(0.75f, 2);
		for (int i = 0; i < 100; i++) {
			IntegerType key1 = new IntegerType(i);
			VarCharType value1 = new VarCharType("num " + i);
			table.put(key1, value1);
		}
		assertEquals("table size 100", 100, table.size());
	}

	@Test
	public void remove100() {
		LinearHashTable table = new LinearHashTable(0.75f, 2);
		int count = 100;
		for (int i = 0; i < count; i++) {
			IntegerType key1 = new IntegerType(i);
			VarCharType value1 = new VarCharType("num " + i);
			table.put(key1, value1);
		}
		for (int i = 0; i < count; i++) {
			IntegerType key2 = new IntegerType(i);
			VarCharType value2 = new VarCharType("num " + i);
			DataType value = table.remove(key2);
			assertEquals("value", value2, value);
		}
		assertEquals("table empty", true, table.isEmpty());
	}

	@Test
	public void testRemove() {
		LinearHashTable table = new LinearHashTable(0.75f, 2);
		IntegerType key1 = new IntegerType(5);
		VarCharType value1 = new VarCharType("five");
		table.put(key1, value1);
		assertEquals("table size increased", 1, table.size());
		IntegerType key2 = new IntegerType(6);
		VarCharType value2 = new VarCharType("six");
		table.put(key2, value2);
		DataType value3 = table.remove(key2);
		assertEquals("remove correct value", value2, value3);
	}

}
