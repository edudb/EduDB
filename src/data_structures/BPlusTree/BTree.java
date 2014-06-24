/*
EduDB is made available under the OSI-approved MIT license.

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/


package data_structures.BPlusTree;

import operators.DBParameter;
import operators.Operator;

import java.util.ArrayList;

/**
 * A B+ tree Since the structures and behaviors between internal node and external node are different,  so there are two different classes for each kind of node.
 * @param  < TKey  >  the data type of the key
 * @param  < TValue  >  the data type of the value
 */
public class BTree<TKey extends Comparable<TKey>, TValue> {
	/**
     * @uml.property  name="root"
     * @uml.associationEnd  multiplicity="(1 1)"
     */
	private BTreeNode<TKey> root;
	/**
     * @uml.property  name="tableName"
     */
	private String tableName;

	public BTree() {
		this.root = new BTreeLeafNode<TKey, TValue>();
	}

	/**
	 * Insert a new key and its associated value into the B+ tree.
	 */
	public void insert(TKey key, TValue value) {
		BTreeLeafNode<TKey, TValue> leaf = this.findLeafNodeShouldContainKey(key);
		leaf.insertKey(key, value);
		
		if (leaf.isOverflow()) {
			BTreeNode<TKey> n = leaf.dealOverflow();
			if (n != null)
				this.root = n; 
		}
	}
	
	/**
	 * Search a key value on the tree and return its associated value.
	 */
	public TValue search(TKey key) {
		BTreeLeafNode<TKey, TValue> leaf = this.findLeafNodeShouldContainKey(key);
		
		int index = leaf.search(key);
		return (index == -1) ? null : leaf.getValue(index);
	}
	
	/**
	 * Delete a key and its associated value from the tree.
	 */
	public void delete(TKey key) {
		BTreeLeafNode<TKey, TValue> leaf = this.findLeafNodeShouldContainKey(key);
		
		if (leaf.delete(key) && leaf.isUnderflow()) {
			BTreeNode<TKey> n = leaf.dealUnderflow();
			if (n != null)
				this.root   = n;
		}
	}
	
	/**
	 * Search the leaf node which should contain the specified key
	 */
	@SuppressWarnings("unchecked")
	private BTreeLeafNode<TKey, TValue> findLeafNodeShouldContainKey(TKey key) {
		BTreeNode<TKey> node = this.root;
		while (node.getNodeType() == TreeNodeType.InnerNode) {
			node = ((BTreeInnerNode<TKey>)node).getChild( node.search(key) );
		}
		
		return (BTreeLeafNode<TKey, TValue>)node;
	}

    public void print(){
    ArrayList<BTreeNode> upper = new ArrayList<>();
    ArrayList<BTreeNode> lower = new ArrayList<>();

    upper.add(root);
    while(!upper.isEmpty() ){
        BTreeNode cur = upper.get(0);
        if(cur instanceof BTreeInnerNode){
            ArrayList<BTreeNode> children = ((BTreeInnerNode) cur).getChildren();
            for (int i = 0; i < children.size(); i++) {
                BTreeNode child = children.get(i);
                if(child != null)
                    lower.add( child );
            }
        }
        System.out.print(cur.toString() + " ");
        upper.remove(0);
        if(upper.isEmpty()){
            System.out.print("\n");
            upper = lower;
            lower = new ArrayList<>();
        }
    }
    }

    public BTreeLeafNode getSmallest(){
        return this.root.getSmallest();
    }

    public String commit() {
        return this.root.commit();
    }
}
