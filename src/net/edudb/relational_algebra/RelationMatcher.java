/*
EduDB is made available under the OSI-approved MIT license.

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

package net.edudb.relational_algebra;

import java.util.regex.Matcher;

import net.edudb.engine.Config;
import net.edudb.operator.RelationOperator;
import net.edudb.structure.table.Table;
import net.edudb.structure.table.TableFactory;

public class RelationMatcher implements RAMatcherChain {
	private RAMatcherChain nextElement;
	private String regex = "\\A(\\w+)\\=(?:Relation\\(.(?:\\,.)*\\))\\z";

	@Override
	public void setNextInChain(RAMatcherChain chainElement) {
		this.nextElement = chainElement;
	}

	@Override
	public RAMatcherResult parse(String string) {
		Matcher matcher = Translator.matcher(string, regex);
		if (matcher.matches()) {
			RelationOperator relationOperator = new RelationOperator();
			/**
			 * Defer reading the actual table until the execution step to
			 * minimize effects.
			 */
			TableFactory tableFactory = new TableFactory();
			Table table = tableFactory.makeTable(Config.tableType(), matcher.group(1));
			relationOperator.setParameter(table);
			return new RAMatcherResult(relationOperator, "");
		}
		return this.nextElement.parse(string);
	}
}
