/*
EduDB is made available under the OSI-approved MIT license.

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/


package operators;

import DBStructure.DBIndex;
import DBStructure.DBRecord;
import DBStructure.DBTable;
import DBStructure.DataManager;
import dataTypes.DB_Type;
import gudusoft.gsqlparser.TCustomSqlStatement;
import gudusoft.gsqlparser.nodes.TResultColumnList;
import gudusoft.gsqlparser.stmt.TInsertSqlStatement;
import transcations.Page;

/**
 * Created by mohamed on 4/11/14.
 */
public class InsertOperator implements Operator{

    /**
     * @uml.property  name="statement"
     * @uml.associationEnd  multiplicity="(1 1)"
     */
    private TInsertSqlStatement statement;

    public InsertOperator(TCustomSqlStatement statement){
        this.statement = (TInsertSqlStatement) statement;
    }

    @Override
    public DBResult execute() {
        System.out.println("executing insert operation");
        DBTable table = DataManager.getTable(statement.getTargetTable().toString());
        if(table == null){
            System.out.println("table doesnot exist");
            return null;
        }
        DBIndex index = table.getPrimaryIndex();
        // TODO value may be null
        TResultColumnList values = statement.getValues().getMultiTarget(0).getColumnList();
        DBRecord record = new DBRecord(values, table.getTableName());
        int key = ( (DB_Type.DB_Int) record.getValue(0) ).getNumber();
        index.insert(key, record);
        index.write();
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
