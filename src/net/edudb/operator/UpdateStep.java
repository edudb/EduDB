/*
EduDB is made available under the OSI-approved MIT license.

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/


package net.edudb.operator;

import net.edudb.operator.Operator;
import net.edudb.page.Page;
import net.edudb.structure.Record;

import java.util.ArrayList;


public class UpdateStep implements Operator{
    private DBIterator iterator;
    private ArrayList<DBAssignment> assignments;

    public UpdateStep(ArrayList<DBAssignment> assignments){
        this.assignments = assignments;
    }

    @Override
    public DBResult execute() {
        Record record = (Record) iterator.first();
        do{
            record.update(assignments);
            record = (Record) iterator.next();
        }while (record != null);
        return iterator;
    }

    @Override
    public DBParameter[] getChildren() {
        return new DBParameter[0];
    }

    @Override
    public void giveParameter(DBParameter par) {
        if (par instanceof DBIterator){
            this.iterator = (DBIterator) par;
        }
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
