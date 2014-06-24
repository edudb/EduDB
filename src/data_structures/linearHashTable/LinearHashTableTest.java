/*
EduDB is made available under the OSI-approved MIT license.

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/


package data_structures.linearHashTable;

import static org.junit.Assert.*;

import dataTypes.DB_Type;
import dataTypes.DataType;
import org.junit.Test;

public class LinearHashTableTest {

	@Test
	public void testPutOneGetOne() {
		LinearHashTable table = new LinearHashTable(0.75f, 2);

        DB_Type.DB_Int key = new DB_Type.DB_Int(5);
		DB_Type.DB_String value = new DB_Type.DB_String();
		value.str = "five";
		table.put(key, value);
		assertTrue(key.equals(key));
		assertEquals("get as put", value,table.get(key));
	}
	
	@Test
	public void testResize() {
		LinearHashTable table = new LinearHashTable(0.75f, 2);
		DB_Type.DB_Int key1 = new DB_Type.DB_Int(5);
		DB_Type.DB_String value1 = new DB_Type.DB_String();
		value1.str = "five";
		table.put(key1, value1);
		assertEquals("table size increased", 1, table.size());
		DB_Type.DB_Int key2 = new DB_Type.DB_Int(6);
		DB_Type.DB_String value2 = new DB_Type.DB_String();
		value2.str = "six";
		table.put(key2, value2);
		assertEquals("table size increased", 2, table.size());
	}

	@Test
	public void put100(){
		LinearHashTable table = new LinearHashTable(0.75f, 2);
		for (int i = 0; i < 100; i++) {
			DB_Type.DB_Int key1 = new DB_Type.DB_Int(i);
			DB_Type.DB_String value1 = new DB_Type.DB_String();
			value1.str = "num "+i;
			table.put(key1, value1);
		}
		assertEquals("table size 100", 100, table.size());
	}
	
	@Test
	public void remove100(){
		LinearHashTable table = new LinearHashTable(0.75f, 2);
        int count = 100;
		for (int i = 0; i < count; i++) {
			DB_Type.DB_Int key1 = new DB_Type.DB_Int(i);
			DB_Type.DB_String value1 = new DB_Type.DB_String();
			value1.str = "num "+i;
			table.put(key1, value1);
		}
		for (int i = 0; i < count; i++) {
			DB_Type.DB_Int key2 = new DB_Type.DB_Int(i);
			DB_Type.DB_String value2 = new DB_Type.DB_String();
			value2.str = "num "+i;
			DataType value = table.remove(key2);
			assertEquals("value", value2, value);
		}
		assertEquals("table empty", true, table.isEmpty());
	}
	@Test
	public void testRemove() {
		LinearHashTable table = new LinearHashTable(0.75f, 2);
		DB_Type.DB_Int key1 = new DB_Type.DB_Int(5);
		DB_Type.DB_String value1 = new DB_Type.DB_String();
		value1.str = "five";
		table.put(key1, value1);
		assertEquals("table size increased", 1, table.size());
		DB_Type.DB_Int key2 = new DB_Type.DB_Int(6);
		DB_Type.DB_String value2 = new DB_Type.DB_String();
		value2.str = "six";
		table.put(key2, value2);
		DataType value3 = table.remove(key2);
		assertEquals("remove correct value", value2, value3);
	}
	
	
	
	
}
