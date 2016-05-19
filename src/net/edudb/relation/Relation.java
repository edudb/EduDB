/*
EduDB is made available under the OSI-approved MIT license.

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

package net.edudb.relation;

import net.edudb.server.ServerWriter;
import net.edudb.structure.table.Table;

/**
 * Holds records returned from the execution of relational algebra operators.
 * 
 * @author Ahmed Abdul Badie
 *
 */
public interface Relation extends Table {

	/**
	 * 
	 * @return An iterator that iterates over the relation's records.
	 */
	public RelationIterator getIterator();

	/**
	 * Prints a given relation to the writer stream iff the relation is not
	 * null. <br>
	 * Uses a Relation Iterator to iterate through the given relation.
	 */
	public static void print(Relation relation) {
		if (relation == null) {
			return;
		}
		RelationIterator relationIterator = relation.getIterator();
		while (relationIterator.hasNext()) {
			ServerWriter.getInstance().writeln(relationIterator.next());
		}
	}

}
