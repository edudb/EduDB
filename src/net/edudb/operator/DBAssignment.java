/*
EduDB is made available under the OSI-approved MIT license.

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/


package net.edudb.operator;

import net.edudb.data_type.DB_Type;
import net.edudb.data_type.DataType;
import net.edudb.statistics.Schema;
import net.edudb.structure.DBRecord;

/**
 * Created by mohamed on 5/20/14.
 */
public class DBAssignment implements DBParameter{

    private DataType value;
    private int order;

    public DBAssignment(String s1, String s2, String tableName) {
        this.order = Schema.getColumnNumber(s1, tableName);
        this.value = new DB_Type.DB_Int(s2);
    }

    @Override
    public void print() {

    }

    @Override
    public int numOfParameters() {
        return 0;
    }

    public void execute(DBRecord dbRecord) {
        dbRecord.setValue(order, value);
    }
}
