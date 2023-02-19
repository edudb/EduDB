/*
EduDB is made available under the OSI-approved MIT license.

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

package net.edudb.relational_algebra;

import java.util.regex.Matcher;

import net.edudb.engine.Utility;
import net.edudb.operator.CartesianProductOperator;
import net.edudb.operator.RelationOperator;

/**
 * Matches the relational algebra CartProd formula.
 * 
 * @author Ahmed Abdul Badie
 *
 */
public class CartesianProductMatcher implements RAMatcherChain {
	private RAMatcherChain nextElement;
	/**
	 * Matches strings of the form: <br>
	 * <br>
	 * <b>CartProd(arg0, arg1)</b> <br>
	 * <br>
	 * and captures <b>arg0</b> and <b>arg1</b> in the matcher's groups one and
	 * two, respectively. <b>arg0</b> is the left relational algebra formula and
	 * <b>arg1</b> is the right relation.
	 */
	private String regex = "\\ACartProd\\((.*\\))\\,(.*)\\)\\z";

	@Override
	public void setNextElementInChain(RAMatcherChain chainElement) {
		this.nextElement = chainElement;
	}

	@Override
	public RAMatcherResult match(String string) {
		Matcher matcher = Utility.getMatcher(string, regex);
		if (matcher.matches()) {
			/**
			 * The right argument of the CartProd relational algebra is always a
			 * relation.
			 */
			RelationMatcher relationMatcher = new RelationMatcher();
			RelationOperator relationOperator = (RelationOperator) relationMatcher.match(matcher.group(2)).getNode();

			CartesianProductOperator cartesianOperator = new CartesianProductOperator();
			cartesianOperator.setRightChild(relationOperator);

			return new RAMatcherResult(cartesianOperator, matcher.group(1));
		}
		return nextElement.match(string);
	}

}
