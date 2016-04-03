/*
EduDB is made available under the OSI-approved MIT license.

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/


package net.edudb.page;

import net.edudb.index.BPlusTree.DBBTree;
import net.edudb.operator.DBResult;
import net.edudb.structure.DBIndex;
import net.edudb.structure.Recordable;
//import net.edudb.structure.DBTable;
import net.edudb.transcation.TimeUtil;

/**
 * Created by mohamed on 5/20/14.
 */
public class DBPage implements DBResult, Pageable {
    private DBPageID id;
    private PageState pageState;
//    private LockState lockState;
    private int lastAccessed;
//    private int readers;
//    private boolean locked;
    private DBIndex tree;
    private String table;

    public DBPage(String table) {
        this.table = table;
    }

    public DBPage() {
    }

    public void free() {
        tree = null;
    }

    public void getAccess(){

    }

    public void setLastAccessed() {
        lastAccessed = TimeUtil.getSeconds();
    }

    public void allocate() {
        tree = new DBBTree(table);
        if (tree instanceof DBBTree){
            ( (DBBTree) tree ).readTable();
        }
    }

    public int getlastAccessed() {
        return lastAccessed;
    }

    public PageState getBufferState() {
        return pageState;
    }

    public DBPage getCopy() {
        DBPage page = new DBPage();
        page.id = id;
        // to make dbbuffermanager main work comment line below
        //page.tree = tree.getCopy();
        return this;
    }

    public DBResult getData(){
        return tree.getIterator();
    }

    public DBPageID getPageId() {
        return id;
    }

    public void write() {
        tree.write();
    }

    public void setPageID(DBPageID id) {
        this.id = id;
    }

    @Override
    public void print() {
        tree.getIterator().print();
    }

    @Override
    public int numOfParameters() {
        return 0;
    }

    public enum PageState{
        clean,
        dirty
    }

    public enum LockState{
        free,
        read,
        write,
    }

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Recordable[] getRecords() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addRecord(Recordable record) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int capacity() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int size() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isFull() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isEmpty() {
		// TODO Auto-generated method stub
		return false;
	}
}
