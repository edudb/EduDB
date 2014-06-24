/*
EduDB is made available under the OSI-approved MIT license.

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/


package DBStructure;

import operators.DBParameter;
import statistics.Schema;

import java.util.ArrayList;

/**
 * Created by mohamed on 4/19/14.
 */
public class DBColumn implements DBParameter{
    /**
     * @uml.property  name="order"
     */
    public int order;
    /**
     * @uml.property  name="tableName"
     */
    public String tableName;
    public DBColumn(int num, String tableName) {
        this.order = num;
        this.tableName = tableName;
    }

    public DBColumn(String name, String tableName) {
        this.order = Schema.getColumnNumber(name, tableName)+1;
        this.tableName = tableName;
    }

    public String toString(){
        return tableName + "." + order;
    }

    @Override
    public void print() {
        System.out.print(tableName + "." + order);
        
    }

    @Override
    public boolean equals(Object o){
        if (o instanceof DBColumn){
            return ((DBColumn) o).order == order
                    && ( (DBColumn) o).tableName.equals(tableName);
        }
        return false;
    }
    @Override
    public int numOfParameters() {
        return 0;
    }
}
