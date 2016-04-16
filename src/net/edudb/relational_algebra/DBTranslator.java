/*
EduDB is made available under the OSI-approved MIT license.

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

package net.edudb.relational_algebra;

import java.util.ArrayList;
import java.util.Stack;

import org.antlr.v4.runtime.misc.ParseCancellationException;
import net.edudb.data_type.IntegerType;
import net.edudb.db_operator.AndCondition;
import net.edudb.db_operator.DBCond;
import net.edudb.db_operator.DBCondition;
import net.edudb.db_operator.DBMulCondition;
import net.edudb.db_operator.DBParameter;
import net.edudb.db_operator.FilterOperator;
import net.edudb.db_operator.JoinOperator;
import net.edudb.db_operator.DBOperator;
import net.edudb.db_operator.OrCondition;
import net.edudb.db_operator.ProjectOperator;
import net.edudb.db_operator.RelationOperator;
import net.edudb.db_operator.SelectColumns;
import net.edudb.db_operator.SortOperator;
import net.edudb.statistics.Schema;
import net.edudb.structure.DBColumn;
import ra.Term;
import adipe.translate.TranslationException;
import adipe.translate.Queries;
//import adipe.translate.sql.parser.SqlParser;

/**
 * Created by mohamed on 4/2/14.
 */
public class DBTranslator {

	public static DBOperator translate(String sqlQuery) {
		// TODO translate algebra to plan
		try {
			Term ra = Queries.getRaOf(adipe.translate.ra.Schema.create(Schema.getInstance().getSchema()), sqlQuery);
			return DBTranslator.extractOperations(ra.toString());
		} catch (TranslationException e) {
			e.printStackTrace();
		}
		return null;
	}

	/*
	 * required stack accumulates operations that need parameters and
	 * conditions, fields , relations pop operators from the stack
	 */
	public static DBOperator extractOperations(String cur) {

		boolean first = true;
		DBOperator out = null;
		Stack<DBOperator> required = new Stack<DBOperator>();
		DBOperator[] given = new DBOperator[2];
		boolean empty = true;
		ArrayList<String> tableNames = new ArrayList<String>();
		ArrayList<Integer> tableCounts = new ArrayList<Integer>();
		DBOperator x = null;
		while (cur.length() > 0) {
			while (cur.length() > 0) {
				if (cur.charAt(0) == ')' || cur.charAt(0) == ',')
					cur = cur.substring(1);
				else
					break;
			}
			if (cur.startsWith("0a0=")) {
				cur = cur.substring(4);
			}
			if (cur.startsWith("Project")) {
				cur = cur.substring(8);
				ProjectOperator project = new ProjectOperator();
				required.push(project);
				if (first) {
					out = project;
					first = false;
				}
			} else if (cur.startsWith("Filter")) {
				cur = cur.substring(7);
				FilterOperator filter = new FilterOperator();
				required.push(filter);
				if (first) {
					out = filter;
					first = false;
				}
			} else if (cur.charAt(0) == '"') {// filter condition
				/******************** extract condition ************/

				String condition = "";
				char c;
				int i = 0;
				while ((c = cur.charAt(++i)) != '"') {
					condition += c;
				}
				DBParameter cond = extractCondition(tableNames, tableCounts, condition);
				cur = cur.substring(i + 2);
				try {// pop filter operator
					FilterOperator top = (FilterOperator) required.pop();
					top.giveParameter(given[0]);
					top.giveParameter(cond);
					empty = false;
					given[0] = top;
					x = top;
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else if (cur.charAt(0) >= 'a' && cur.charAt(0) <= 'z') {// table
																		// name
				RelationOperator relation = new RelationOperator();
				/********************** extract relation ********************/
				int i = 0;
				String tableName = "";
				char c;
				while ((c = cur.charAt(i++)) != '=') {
					tableName += c;
				}
				while ((c = cur.charAt(i++)) != '(') {
				}
				int count = 0;
				while ((c = cur.charAt(i++)) != ')') {
					if (cur.charAt(i - 1) != ',') {
						count++;
					}
				}
				// System.out.println("count " + count);
				if (i < cur.length() - 1) {
					cur = cur.substring(++i);
				} else {
					cur = "";
				}
				tableNames.add(tableName);
				tableCounts.add(count);
				relation.setTableName(tableName);
				if (first) {
					out = relation;
					first = false;
				}
				x = relation;
				/********************** pop operators ********************/
				while (!required.empty()) {
					if (empty) {
						if (required.peek().numOfParameters() == 1) {
							DBOperator top = required.pop();
							top.giveParameter(x);
							x = top;
						} else {
							given[0] = x;
							empty = false;
							break;
						}
					} else {
						DBOperator top = required.pop();
						top.giveParameter(x);
						top.giveParameter(given[0]);
						x = top;
						empty = true;
					}
				}
			} else if (cur.startsWith("CartProd")) {
				cur = cur.substring(9);
				JoinOperator join = new JoinOperator();
				required.push(join);
				if (first) {
					out = join;
					first = false;
				}
			} else if (cur.startsWith("SortAsc")) {
				cur = cur.substring(8);
				SortOperator sort = new SortOperator();
				required.push(sort);
			} else if (cur.startsWith("[")) {// column list
				cur = cur.replaceAll("\\s", "");
				int i = 1;
				ArrayList<DBColumn> columns = new ArrayList<DBColumn>();
				while (i < cur.length() && cur.charAt(i) <= '9' && cur.charAt(i) >= '0') {
					int num = cur.charAt(i) - '0';
					int tmp = 0;
					i += 2;
					String tableName;
					while (true) {
						if (num > tableCounts.get(tmp)) {
							num -= tableCounts.get(tmp++);
						} else {
							tableName = tableNames.get(tmp);
							break;
						}
					}
					DBColumn column = new DBColumn(num, "", tableName);
					columns.add(column);
				}

				ProjectOperator operator = (ProjectOperator) required.pop();
				SelectColumns columns2 = new SelectColumns(columns);
				operator.giveParameter(columns2);
				operator.giveParameter(given[0]);
				given[0] = operator;
				empty = false;
				if (i >= cur.length()) {
					cur = "";
				} else {
					cur = cur.substring(--i);
				}
				cur = cur.substring(2);
			}
		}
		return out;
	}

	// TODO constants in cond
	private static DBParameter extractCondition(ArrayList<String> names, ArrayList<Integer> counts, String condition) {
		DBCond x = null;
		DBCond given = null;
		Stack<DBParameter> required = new Stack<>();
		while (condition.length() > 0) {
			while (condition.length() > 0) {
				if (condition.charAt(0) == ')' || condition.charAt(0) == ',')
					condition = condition.substring(1);
				else
					break;
			}
			if (condition.startsWith("OR")) {
				condition = condition.substring(3);
				OrCondition orOperation = new OrCondition();
				required.push(orOperation);
			} else if (condition.startsWith("AND")) {
				condition = condition.substring(4);
				AndCondition andOperation = new AndCondition();
				required.push(andOperation);
			} else if (condition.startsWith("#")) {// parameter
				int num = condition.charAt(1) - '0';
				// TODO num could be > 9
				int i = 0;
				String tableName = null;
				while (true) {
					if (num > counts.get(i)) {
						num -= counts.get(i++);
					} else {
						tableName = names.get(i);
						break;
					}
				}
				DBColumn column = new DBColumn(num, "", tableName);
				char op = condition.charAt(2);
				char right = condition.charAt(3);
				DBCondition condition2;
				if (right == '#') {
					num = condition.charAt(4) - '0';
					condition = condition.substring(5);
					// TODO num could be > 9
					i = 0;
					while (true) {
						if (num > counts.get(i)) {
							num -= counts.get(i++);
						} else {
							tableName = names.get(i);
							break;
						}
					}
					DBColumn column2 = new DBColumn(num, "", tableName);
					condition2 = new DBCondition(column, column2, op);
				} else {
					int index = 3;
					char ch = condition.charAt(index);
					int param = 0;
					while (ch >= '0' && ch <= '9') {
						param *= 10;
						param += ch - '0';
						index++;
						if (index == condition.length())
							break;
						ch = condition.charAt(index);
					}
					IntegerType c = new IntegerType(param);
					condition2 = new DBCondition(column, c, op);
					if (index == condition.length())
						condition = "";
					else
						condition = condition.substring(index + 1);
				}
				x = condition2;
				if (given == null) {
					if (!required.empty()) {
						if (required.peek().numOfParameters() == 1) {
							DBParameter top = required.pop();
							((DBMulCondition) top).setParameter(x);
							x = (DBCond) top;
						} else {
							given = x;
						}
					}
				} else {
					DBParameter top = required.pop();
					((DBMulCondition) top).setParameter(x);
					((DBMulCondition) top).setParameter(given);
					x = (DBCond) top;
					given = x;
				}
			}
		}
		return x;
	}

	public static void main(String[] args) throws Exception {
		String s[] = { "select * from persons", "select age from persons",
				"select * from persons where age>31 OR age=32", "select age from persons where age>12",
				"select age from persons where age>12 order by age desc", "select number from persons group by number",
				"select count(age), count(number) from persons", "select age,id from persons, places where age = 32",
				"select birth from persons inner join places on age = id",
				"select p1.birth from persons p1 inner join places on p1.age = id inner join persons p on p.age = p1.number",
				"select * from persons where exists (select id from places where id=30)",
				"select age from persons, places where number>=id", 
				"select age from persons, places where age=5" };
		Term ra2;
		try {
			for (int i = 0; i < s.length; i++) {
				ra2 = Queries.getRaOf(adipe.translate.ra.Schema.create(Schema.getInstance().getSchema()), s[i]);
				System.out.format("*** SQL query:\n\t%s --> %s\n", s[i], ra2);
			}
		} catch (ParseCancellationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TranslationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
