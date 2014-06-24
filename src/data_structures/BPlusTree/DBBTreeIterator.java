/*
EduDB is made available under the OSI-approved MIT license.

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/


package data_structures.BPlusTree;

import DBStructure.DBRecord;
import operators.DBCond;
import operators.DBIterator;
import operators.DBResult;
import operators.SelectColumns;

import java.util.ArrayList;
import java.util.ListIterator;

/**
 * Created by mohamed on 5/12/14.
 */
public class DBBTreeIterator implements ListIterator,
        DBResult , DBIterator{
    /**
     * @uml.property  name="tree"
     * @uml.associationEnd  multiplicity="(1 1)"
     */
    DBBTree tree;
    /**
     * @uml.property  name="cur"
     * @uml.associationEnd  multiplicity="(1 1)"
     */
    BTreeLeafNode cur;
    /**
     * @uml.property  name="index"
     */
    private int currentkey;

    private int index;
    /**
     * @uml.property  name="columns"
     * @uml.associationEnd  
     */
    SelectColumns columns;
    /**
     * @uml.property  name="conditions"
     * @uml.associationEnd  multiplicity="(0 -1)" elementType="operators.DBCond"
     */
    private ArrayList<DBCond> conditions;

    public DBBTreeIterator(DBBTree tree){
        this.tree = tree;
        this.cur = tree.getSmallest();
        this.index = 0;
        conditions = new ArrayList<>();
        this.currentkey = 0;
    }

    public void project(SelectColumns columns){
        if (this.columns == null){
            this.columns = columns;
        }else {
            this.columns.union(columns);
        }
    }
    @Override
    public boolean hasNext() {
        if(cur == null)
            return false;
        return cur.rightSibling != null
                || currentkey < cur.getKeyCount()-1;
    }

    @Override
    public Object next() {
        if (cur == null){
            return null;
        }
        currentkey++;
        if(currentkey< cur.getKeyCount()){
            if(! ((DBRecord) cur.getValue(currentkey)).evaluate(conditions).equals(""))
                return cur.getValue(currentkey);
            else
                return next();
        }else {
            currentkey = 0;
            cur = (BTreeLeafNode) cur.rightSibling;
            if (cur== null){
                return null;
            }
            if(! ((DBRecord) cur.getValue(0)).evaluate(conditions).equals(""))
                return cur.getValue(0);
            else
                return next();
        }
    }

    private Object nextNode() {
        cur = (BTreeLeafNode) cur.rightSibling;
        return cur;
    }

    @Override
    public boolean hasPrevious() {
        return cur.leftSibling != null;
    }

    @Override
    public Object previous() {
        return cur.leftSibling;
    }

    @Override
    public int nextIndex() {
        return index++;
    }

    @Override
    public int previousIndex() {
        return index--;
    }

    @Override
    public void remove() {

    }

    @Override
    public void set(Object o) {

    }

    @Override
    public void add(Object o) {

    }

    @Override
    public String toString(){
        String out = "";
        DBBTreeIterator itr = this;
        goToFirst();
        do {
            BTreeNode element = (BTreeNode) itr.cur;
            element.filter(conditions);
            out+= (element.project(columns));
        }while (itr.nextNode() != null);
        return out;
    }



    @Override
    public void print() {
        System.out.print(this);
    }

    @Override
    public int numOfParameters() {
        return 0;
    }

    public void filter(DBCond condition) {
        this.conditions.add(condition);
    }

    public Object first() {
        cur = tree.getSmallest();

        if (cur == null){
            System.out.println("1");
        }
        if (cur.getValue(0) == null){
            System.out.println(cur);
        }
        if(! ((DBRecord) cur.getValue(0)).evaluate(conditions).equals(""))
            return cur.getValue(0);
        else
            return next();
    }

    public void goToFirst() {
        cur = tree.getSmallest();
        index = 0;
    }

    public void write() {
        tree.write();
    }
}
