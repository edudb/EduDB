/*
 *
 * EduDB is made available under the OSI-approved MIT license.
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 * /
 */

package net.edudb.relation;

import net.edudb.buffer.BufferManager;
import net.edudb.engine.Utility;
import net.edudb.page.Page;
import net.edudb.structure.Record;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Iterates over a relation's records.
 *
 * @author Ahmed Abdul Badie
 */
public class RelationIterator implements Iterator<Record>, AutoCloseable {
    private String id;
    private final ArrayList<String> pageNames;
    private Page currentPage;
    private int currentPageIndex;
    private int currentIndex;
    private boolean hasLock;

    public RelationIterator(ArrayList<String> pageNames) {
        this.pageNames = pageNames;
        this.id = Utility.generateUUID();

        if (this.pageNames.isEmpty()) return;

        acquirePagesLock();

        this.currentPageIndex = 0;
        this.currentIndex = 0;
        this.currentPage = BufferManager.getInstance().read(pageNames.get(currentPageIndex++));
    }

    @Override
    public void close() {
        releasePagesLock();
    }

    public void releasePagesLock() {
        for (String pageName : pageNames) {
            Page page = BufferManager.getInstance().read(pageName);
            page.releaseLock();
        }
    }


    public void acquirePagesLock() {
        for (String pageName : pageNames) {
            Page page = BufferManager.getInstance().read(pageName);
            page.acquireLock();
        }
    }

    public void reset() {
        if (this.pageNames.isEmpty()) return;
        this.currentPageIndex = 0;
        this.currentIndex = 0;
        this.currentPage = BufferManager.getInstance().read(pageNames.get(currentPageIndex++));
    }


    @Override
    public boolean hasNext() {
        /**
         * Relation has no pages; empty relation.
         */
        if (currentPage == null) {
            return false;
        }
        /**
         * Current index is greater than the size of the current page; iterated
         * through the whole page.
         */
        if (currentIndex > 0 && currentIndex >= currentPage.size()) {
            /**
             * Relation has more pages to iterate through.
             */
            if (currentPageIndex < pageNames.size()) {
                this.currentPage = BufferManager.getInstance().read(pageNames.get(currentPageIndex++));
                this.currentIndex = 0;
            }
        }
        /**
         * This loop is used to skip through the deleted records.
         */
        for (int i = currentIndex; i < currentPage.size(); i++) {
            /**
             * Records is not deleted; should be returned.
             */
            if (!currentPage.getRecord(i).isDeleted()) {
                break;
            }
            currentIndex++;
        }
        if (currentIndex < currentPage.size()) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public Record next() {
        if (!hasNext()) {
            return null;
        }
        return currentPage.getRecord(currentIndex++);
    }

    public List<Record> next(int n) {
        List<Record> records = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            Record record = next();
            if (record == null) {
                break;
            }
            records.add(record);
        }
        return records;
    }


    public String getId() {
        return id;
    }

    public Page getCurrentPage() {
        return currentPage;
    }
}
