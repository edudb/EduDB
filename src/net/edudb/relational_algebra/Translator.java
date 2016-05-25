/*
EduDB is made available under the OSI-approved MIT license.

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

package net.edudb.relational_algebra;

import java.util.ArrayList;
import java.util.Collections;
import adipe.translate.Queries;
import adipe.translate.TranslationException;
import net.edudb.ebtree.EBNode;
import net.edudb.ebtree.EBTree;
import net.edudb.query.QueryTree;
import net.edudb.server.ServerWriter;
import net.edudb.statistics.Schema;
import ra.Term;

public class Translator {
	/**
	 * Translates an SQL SELECT statement into a relational algebra formula.
	 * 
	 * @param sqlString
	 *            The SQL statement to translate.
	 * @return The relational algebra formula.
	 */
	public String translate(String sqlString) {
		try {
			Term term = Queries.getRaOf(adipe.translate.ra.Schema.create(Schema.getInstance().getSchema()), sqlString);
			System.out.println("Translator (teanslate):" + term.toString());
			return term.toString();
		} catch (RuntimeException | TranslationException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Returns a chain of {@link RAMatcherChain} elements that are used to match
	 * a relational algebra formula.
	 * 
	 * @return Chain of {@link RAMatcherChain} elements.
	 */
	private RAMatcherChain getChain() {
		RAMatcherChain project = new ProjectMatcher();
		RAMatcherChain cartesian = new CartesianProductMatcher();
		RAMatcherChain equi = new EquiJoinMatcher();
		RAMatcherChain filter = new FilterMatcher();
		RAMatcherChain relation = new RelationMatcher();

		project.setNextElementInChain(cartesian);
		cartesian.setNextElementInChain(equi);
		equi.setNextElementInChain(filter);
		filter.setNextElementInChain(relation);
		relation.setNextElementInChain(new NullMatcher());
		return project;
	}

	/**
	 * Creates query tree nodes from a relational algebra formula by using
	 * string matchers.
	 * 
	 * @param relationAlgebra
	 *            The relational algebra formula to match.
	 * @param chain
	 *            The string matcher chain.
	 * @return List of query tree nodes.
	 */
	private ArrayList<EBNode> constructTreeNodes(String relationAlgebra, RAMatcherChain chain) {
		ArrayList<EBNode> nodes = new ArrayList<>();
		while (relationAlgebra.length() != 0) {
			RAMatcherResult result = chain.match(relationAlgebra);
			if (result != null) {
				nodes.add(result.getNode());
				relationAlgebra = result.getString();
			} else {
				ServerWriter.getInstance().writeln("No Match");
				break;
			}
		}
		return nodes;
	}

	/**
	 * Creates a query tree by matching a relational algebra formula.
	 * 
	 * @param relationAlgebra
	 *            The relational algebra formula to match.
	 * @return The created query tree.
	 */
	public QueryTree processRelationalAlgebra(String relationAlgebra) {
		ArrayList<EBNode> nodes = constructTreeNodes(relationAlgebra, getChain());
		Collections.reverse(nodes);
		EBTree tree = new QueryTree();
		tree.constructTree(nodes);

		return (QueryTree) tree;
	}

}
