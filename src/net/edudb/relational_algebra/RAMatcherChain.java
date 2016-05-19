/*
EduDB is made available under the OSI-approved MIT license.

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

package net.edudb.relational_algebra;

/**
 * An interface, that implements the Chain of Responsibility design pattern,
 * that is used to match relational algebra formulae.
 * 
 * @author Ahmed Abdul Badie
 *
 */
public interface RAMatcherChain {

	/**
	 * Connects an existing executor chain with the argument chain element.
	 * 
	 * @param chainElement
	 *            The {@link RAMatcherChain} to be set next in the chain.
	 */
	public void setNextElementInChain(RAMatcherChain chainElement);

	/**
	 * Matches the passed relational algebra formula. Classes that implement the
	 * {@link RAMatcherChain} interface handles the matching of the passed
	 * relational algebra formula according to their functionality.
	 * 
	 * @param string
	 *            The relational algebra formula to match.
	 * 
	 * @return The result of matching the passed relational algebra formula.
	 */
	public RAMatcherResult match(String string);
}
