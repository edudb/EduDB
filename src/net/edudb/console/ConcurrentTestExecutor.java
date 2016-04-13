/*
EduDB is made available under the OSI-approved MIT license.

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

package net.edudb.console;

import adipe.translate.TranslationException;
import net.edudb.parser.Parser;
import net.edudb.relation.Relation;
import net.edudb.relation.RelationIterator;
import net.edudb.relation.VolatileRelation;
import net.edudb.structure.table.Table;
import net.edudb.structure.table.TableManager;

public class ConcurrentTestExecutor implements ConsoleExecutorChain {
	private ConsoleExecutorChain nextChainElement;

	@Override
	public void setNextInChain(ConsoleExecutorChain chainElement) {
		this.nextChainElement = chainElement;
	}

	@Override
	public void execute(String string) {
		if (string.equalsIgnoreCase("test")) {
//			Parser parser = new Parser();
//			try {
//				parser.parseSQL("create table test (a integer)");
//				Thread.sleep(1000);
//				for (int i = 0; i < 10; i++) {
//					parser.parseSQL("insert into test values(" + (i + 1) + ")");
//				}
//
//			} catch (TranslationException | InterruptedException e) {
//				e.printStackTrace();
//			}

			Table table = TableManager.getInstance().read("test");
			
			Relation r = new VolatileRelation(table);
			RelationIterator rit = r.getIterator();
			
			while(rit.hasNext()) {
				System.out.println(rit.next());
			}
			return;
		}
		nextChainElement.execute(string);
	}

}
