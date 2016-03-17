/*
EduDB is made available under the OSI-approved MIT license.

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

package net.edudb.index.kDTree;

import org.junit.Test;

import net.edudb.data_type.DB_Type;
import net.edudb.data_type.DataType;

/**
 * Created by mohamed on 3/23/14.
 */
public class KDTreeTest {

	@Test
	public void testConstructor() {
		KDTree tree = new KDTree(2);
	}

	@Test
	public void test10Insert() {
		KDTree tree = new KDTree(2);
		DataType[] key = new DataType[2];
		for (int i = 0; i < 10; i++) {
			key[0] = new DB_Type.DB_Int(i);
			key[1] = new DB_Type.DB_Char((char) ('a' + i));
			DataType value = new DB_Type.DB_Int(100);
			tree.insert(key, value);
		}
	}

	@Test
	public void testSearch() {
		KDTree tree = new KDTree(2);
		DataType[] key = new DataType[2];
		for (int i = 0; i < 10; i++) {
			key[0] = new DB_Type.DB_Int(i);
			key[1] = new DB_Type.DB_Char((char) ('a' + i));
			DataType value = new DB_Type.DB_Int(i + 5);
			tree.insert(key, value);
		}
		for (int i = 0; i < 10; i++) {
			key[0] = new DB_Type.DB_Int(i);
			key[1] = new DB_Type.DB_Char((char) ('a' + i));
			Object value = tree.search(key);
			assert value != null;
			DB_Type.DB_Int intValue = (DB_Type.DB_Int) value;
			assert intValue.number == i + 5;
		}
	}

	@Test
	public void testDelete() {
		KDTree tree = new KDTree(2);
		DataType[] key = new DataType[2];
		for (int i = 0; i < 10; i++) {
			key[0] = new DB_Type.DB_Int(i);
			key[1] = new DB_Type.DB_Char((char) ('a' + i));
			DataType value = new DB_Type.DB_Int(i + 5);
			tree.insert(key, value);
		}
		for (int i = 0; i < 10; i++) {
			key[0] = new DB_Type.DB_Int(i);
			key[1] = new DB_Type.DB_Char((char) ('a' + i));
			tree.delete(key);
		}
	}

	@Test
	public void testRange() {
		KDTree tree = new KDTree(2);
		DataType[] key = new DataType[2];
		for (int i = 0; i < 10; i++) {
			key[0] = new DB_Type.DB_Int(i);
			key[1] = new DB_Type.DB_Char((char) ('a' + i));
			DataType value = new DB_Type.DB_Int(i + 5);
			tree.insert(key, value);
		}
		DataType[] lowk = new DataType[2];
		lowk[0] = new DB_Type.DB_Int(3);
		lowk[1] = new DB_Type.DB_Char('d');
		DataType[] uppk = new DataType[2];
		uppk[0] = new DB_Type.DB_Int(6);
		uppk[1] = new DB_Type.DB_Char('g');
		Object[] values = tree.range(lowk, uppk);
		assert values.length == 4;
	}

}