/*
EduDB is made available under the OSI-approved MIT license.

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

package net.edudb.relational_algebra;

import java.util.regex.Matcher;

import net.edudb.expression.Expression;
import net.edudb.expression.OperatorType;
import net.edudb.operator.EquiJoinOperator;
import net.edudb.operator.RelationOperator;
import net.edudb.structure.DBColumn;

public class EquiJoinMatcher implements RAMatcherChain {
	private RAMatcherChain nextElement;
	private String regex = "\\AEqJoin\\((.*\\)),(.*)\\,(\\d+)\\,(\\d+)\\)\\z";

	@Override
	public void setNextElementInChain(RAMatcherChain chainElement) {
		this.nextElement = chainElement;
	}

	@Override
	public RAMatcherResult match(String string) {
		Matcher matcher = Translator.getMatcher(string, regex);
		if (matcher.matches()) {
			/**
			 * The second argument of the EqJoin relational algebra is always a
			 * relation.
			 */
			RelationMatcher relationMatcher = new RelationMatcher();
			RelationOperator relationOperator = (RelationOperator) relationMatcher.match(matcher.group(2)).getNode();

			EquiJoinOperator equiOperator = new EquiJoinOperator();
			equiOperator.setRightChild(relationOperator);

			Expression expression = new Expression(new DBColumn(Integer.parseInt(matcher.group(3))),
					new DBColumn(Integer.parseInt(matcher.group(4))), OperatorType.Equal);

			equiOperator.setParameter(expression);

			return new RAMatcherResult(equiOperator, matcher.group(1));
		}
		return nextElement.match(string);
	}

}
