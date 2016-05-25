/*
EduDB is made available under the OSI-approved MIT license.

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

package net.edudb.relational_algebra;

import java.util.regex.Matcher;

import net.edudb.engine.Utility;
import net.edudb.operator.ProjectOperator;
import net.edudb.operator.parameter.ProjectOperatorParameter;

/**
 * Matches the relational algebra Project formula.
 * 
 * @author Ahmed Abdul Badie
 *
 */
public class ProjectMatcher implements RAMatcherChain {
	private RAMatcherChain nextElement;
	/**
	 * Matches strings of the form: <br>
	 * <br>
	 * <b>Project(arg0, arg1)</b> <br>
	 * <br>
	 * and captures <b>arg0</b> and <b>arg1</b> in the matcher's groups one and
	 * two, respectively. <b>arg0</b> is the relational algebra formula to
	 * project the columns from and <b>arg1</b> is the column orders to project.
	 */
	private String regex = "\\AProject\\((.+)\\,\\[(\\d+(?:\\,\\s\\d+)*)\\]\\)\\z";

	@Override
	public void setNextElementInChain(RAMatcherChain chainElement) {
		this.nextElement = chainElement;
	}

	@Override
	public RAMatcherResult match(String string) {
		Matcher matcher = Utility.getMatcher(string, regex);
		if (matcher.matches()) {
			ProjectOperator projectOperator = new ProjectOperator();

			String[] columns = matcher.group(2).split(",");
			Integer[] projectedColumns = new Integer[columns.length];
			for (int i = 0; i < projectedColumns.length; i++) {
				projectedColumns[i] = Integer.parseInt(columns[i].trim());
			}

			projectOperator.setParameter(new ProjectOperatorParameter(projectedColumns));

			return new RAMatcherResult(projectOperator, matcher.group(1));
		}
		return this.nextElement.match(string);
	}

}
