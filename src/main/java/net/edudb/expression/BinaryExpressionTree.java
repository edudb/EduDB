/*
EduDB is made available under the OSI-approved MIT license.

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

package net.edudb.expression;

import java.util.LinkedHashMap;
import net.edudb.data_type.DataType;
import net.edudb.ebtree.EBNode;
import net.edudb.ebtree.EBTree;
import net.edudb.structure.Column;

/**
 * A tree that consists of binary expression nodes. Used for expression trees
 * that are evaluated against records.
 * 
 * @author Ahmed Abdul Badie
 *
 */
public class BinaryExpressionTree extends EBTree implements ExpressionTree {

	public BinaryExpressionTree(BinaryExpressionNode root) {
		super(root);
	}

	@Override
	public void addNode(EBNode node) {
		if (this.root != null) {
			this.root.setParent(node);
			((BinaryExpressionNode) node).setLeftChild(this.root);
			this.root = node;
			++this.levels;
			return;
		}
		setRoot(node);
	}

	@Override
	public boolean evaluate(LinkedHashMap<Column, DataType> data) {
		return ((BinaryExpressionNode) this.root).evaluate(data);
	}

}
