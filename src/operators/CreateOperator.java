/*
EduDB is made available under the OSI-approved MIT license.

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/


package operators;

import statistics.Schema;
import gudusoft.gsqlparser.stmt.TCreateTableSqlStatement;
import transcations.Page;

/**
 * Created by mohamed on 4/1/14.
 */
public class CreateOperator implements Operator {

    /**
     * @uml.property  name="statement"
     * @uml.associationEnd  multiplicity="(1 1)"
     */
    private TCreateTableSqlStatement statement;

    public CreateOperator(TCreateTableSqlStatement statement){
        this.statement = statement;
    }
    @Override
    public DBResult execute() {
        System.out.println("executing create operation");
        // add table to schema
        String line = statement.getTableName().toString();
        line += " "+ statement.getColumnList().toString();
        System.out.println("@create operation " + line);
        Schema.AddTable(line);
        //create table file and folder
        //FileManager.createTable(statement.getTableName().toString());
        return null;
    }

    @Override
    public DBParameter[] getChildren() {
        return new DBParameter[0];
    }

    @Override
    public void giveParameter(DBParameter par) {

    }

    @Override
    public void runStep(Page page) {

    }

    @Override
    public Page getPage() {
        return null;
    }


    @Override
    public void print() {

    }

    @Override
    public int numOfParameters() {
        return 0;
    }
}
