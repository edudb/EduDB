/*
EduDB is made available under the OSI-approved MIT license.

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/


package data_structures.BPlusTree;

public class BTreeTest {
	public static void main(String args[]){
		IntegerBTree tree = new IntegerBTree();

		tree.insert(10);
		tree.insert(48);
		tree.insert(23);
		tree.insert(33);
		tree.insert(12);
		
		tree.insert(50);
		
		tree.insert(15);
		tree.insert(18);
		tree.insert(20);
		tree.insert(21);
		tree.insert(31);
		tree.insert(45);
		tree.insert(47);
		tree.insert(52);
		
		tree.insert(30);
		
		tree.insert(19);
		tree.insert(22);
		
		tree.insert(11);
		tree.insert(13);
		tree.insert(16);
		tree.insert(17);
		
		
		tree.insert(1);
		tree.insert(2);
		tree.insert(3);
		tree.insert(4);
		tree.insert(5);
		tree.insert(6);
		tree.insert(7);
		tree.insert(8);
		tree.insert(9);
		
		tree.print();
        //DBBTreeIterator iterator = new DBBTreeIterator(tree);
        //iterator.print();
		return;
	}
}


class IntegerBTree extends BTree<Integer, Integer> {
	public void insert(int key) {
		this.insert(key, key);
	}
	
	public void remove(int key) {
		this.delete(key);
	}
}