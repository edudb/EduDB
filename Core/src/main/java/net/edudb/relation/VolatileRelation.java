/*
 *
 * EduDB is made available under the OSI-approved MIT license.
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 * /
 */

package net.edudb.relation;

import net.edudb.page.PageManager;
import net.edudb.structure.Record;
import net.edudb.structure.table.Table;

import java.io.Serializable;
import java.util.Map;

/**
 * A relation that is not intended to be saved to disk.
 *
 * @author Ahmed Abdul Badie
 */
public class VolatileRelation implements Relation, Serializable {

    private Table table;
    private final PageManager pageManager;

    public VolatileRelation() {
        this.pageManager = new PageManager();
    }

    public VolatileRelation(Table table) {
        this.table = table;
        this.pageManager = table.getPageManager();
    }

    @Override
    public RelationIterator getIterator() {
        return new RelationIterator(pageManager.getPageNames());
    }

    @Override
    public String getName() {
        if (table == null) {
            return null;
        }
        return table.getName();
    }

    @Override
    public PageManager getPageManager() {
        return pageManager;
    }

    @Override
    public String addRecord(Record record) {
        return pageManager.addRecord(record);
    }

    @Override
    public void deletePages() {
        this.pageManager.deletePages();
    }

    @Override
    public void setColumnTypes(Map<String, String> columnTypes) {
        if (table == null) {
            return;
        }
        table.setColumnTypes(columnTypes);
    }

    @Override
    public Map<String, String> getColumnTypes() {
        if (table == null) {
            return null;
        }
        return table.getColumnTypes();
    }

    @Override
    public void print() {
        pageManager.print();
    }

}
