/*
EduDB is made available under the OSI-approved MIT license.

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

package net.edudb.db_operator;

import net.edudb.db_operator.DBOperator;
import net.edudb.page.DBPage;
import net.edudb.server.ServerWriter;
import net.edudb.structure.DBTable;
import net.edudb.structure.DBDataManager;

/**
 * Created by mohamed on 4/13/14.
 */
public class RelationOperator implements DBOperator {

	/**
	 * @uml.property name="tableName"
	 */
	private String tableName;

	/**
	 * @param tableName
	 * @uml.property name="tableName"
	 */
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	@Override
	public DBResult execute() {
		DBTable table = DBDataManager.getTable(tableName);
		return table.getData();
	}

	@Override
	public void print() {
		ServerWriter.getInstance().write(execute());
	}

	@Override
	public String toString() {
		return "table(" + tableName + ")";
	}

	@Override
	public int numOfParameters() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public DBParameter[] getChildren() {
		return new DBParameter[] {};
	}

	@Override
	public void giveParameter(DBParameter par) {
		// TODO Auto-generated method stub

	}

	@Override
	public void runStep(DBPage page) {

	}

	@Override
	public DBPage getPage() {
		return null;
	}
}
