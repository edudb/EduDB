/*
EduDB is made available under the OSI-approved MIT license.

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

package net.edudb.ebtree;

import java.util.ArrayList;

/**
 * A custom-made tree that is used to build query and expression trees.
 * 
 * @author Ahmed Abdul Badie
 *
 */
public abstract class EBTree {
	protected int levels;
	protected EBNode root;

	public EBTree() {
		this.levels = 0;
	}

	public EBTree(EBNode root) {
		this.root = root;
		this.levels = 1;
	}

	/**
	 * 
	 * @return The levels of the tree.
	 */
	public int getLevels() {
		return levels;
	}

	/**
	 * 
	 * @return The root of the tree.
	 */
	public EBNode getRoot() {
		return root;
	}

	/**
	 * Sets the root iff the root is null.
	 * 
	 * @param root
	 *            The root to be set.
	 */
	public void setRoot(EBNode root) {
		if (this.root == null) {
			this.root = root;
			++this.levels;
		}
	}

	/**
	 * Adds a node to the tree using bottom-up approach. The root is given a
	 * parent and then the new parent becomes the root. If the new node is a
	 * binary node, the root becomes the node's left child.
	 * 
	 * @param node
	 *            The node to add.
	 */
	public abstract void addNode(EBNode node);

	/**
	 * A new tree is constructed from the input ArrayList using
	 * {@link #addNode(EBNode) addNode}.
	 * 
	 * @param nodes
	 *            ArrayList of nodes used to construct the tree.
	 * @return The tree constructed using the nodes.
	 */
	public void constructTree(ArrayList<EBNode> nodes) {
		if (nodes.size() == 0) {
			return;
		}
		setRoot(nodes.get(0));
		for (int i = 1; i < nodes.size(); i++) {
			this.addNode(nodes.get(i));
		}
	}
}
