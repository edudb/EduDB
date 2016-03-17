/*
EduDB is made available under the OSI-approved MIT license.

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/


package net.edudb.operator;

import net.edudb.operator.Operator;
import net.edudb.transcation.Page;

/**
 * Created by mohamed on 4/2/14.
 */
public class ProjectOperator implements Operator{

    /**
     * @uml.property  name="columns"
     * @uml.associationEnd  
     */
    SelectColumns columns;
    /**
     * @uml.property  name="tableDbParameter"
     * @uml.associationEnd  
     */
    DBParameter tableDbParameter;

    public ProjectOperator() {
    }

    @Override
    public DBResult execute(){
        DBResult dbResult = ((Operator) tableDbParameter).execute();
        if(dbResult instanceof DBIterator){
            DBIterator iter = (DBIterator) dbResult;
            iter.project(columns);
            return iter;
        }
        System.out.println("project: 24 not iterator\n");
        return null;
    }

    @Override
    public void print() {
        System.out.print(execute());
    }

    @Override
    public String toString(){
        return "project " + columns.toString()+ " ";
    }
    @Override
    public int numOfParameters() {
        return 2;
    }

    @Override
    public DBParameter[] getChildren() {
        return new DBParameter[] {tableDbParameter};
    }

    @Override
    public void giveParameter(DBParameter par) {
        if(par instanceof SelectColumns)
            columns = (SelectColumns) par;
        else
            tableDbParameter = par;
    }

    @Override
    public void runStep(Page page) {

    }

    @Override
    public Page getPage() {
        return null;
    }
}
