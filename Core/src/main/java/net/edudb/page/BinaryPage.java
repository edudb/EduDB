/*
 *
 * EduDB is made available under the OSI-approved MIT license.
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 * /
 */

package net.edudb.page;

import net.edudb.engine.Config;
import net.edudb.engine.Utility;
import net.edudb.exception.LockIsNotAcquiredException;
import net.edudb.structure.Record;

import java.io.Serial;
import java.io.Serializable;
import java.util.concurrent.locks.ReentrantLock;

/**
 * A page that is saved to disk as binary data.
 *
 * @author Ahmed Abdul Badie
 */
public class BinaryPage extends Page implements Serializable {
    @Serial
    private static final long serialVersionUID = 4813060042690551966L;

    private final String name;
    private final ReentrantLock lock;
    private final Record[] records;
    private int nextLocation;

    /**
     * Used to monitor how many threads are currently using the page. Used by
     * the buffer manager to allow the page to reside in the pool if it must be
     * replaced.
     */

    public BinaryPage() {
        this.name = Utility.generateUUID();
        this.lock = new ReentrantLock();
        this.records = new Record[Config.PAGE_SIZE];
        this.nextLocation = 0;
    }


    @Override
    public void acquireLock() {
        this.lock.lock();
    }

    @Override
    public void releaseLock() {
        this.lock.unlock();
    }

    @Override
    public Record getRecord(int index) {
        if (!lock.isHeldByCurrentThread()) {
            RuntimeException e = new LockIsNotAcquiredException("Lock is not acquired on page " + name);
            e.printStackTrace();
            throw e;
        }
        return records[index];
    }

    @Override
    public synchronized void addRecord(Record record) {
        if (!lock.isHeldByCurrentThread()) {
            RuntimeException e = new LockIsNotAcquiredException("Lock is not acquired on page " + name);
            e.printStackTrace();
            throw e;
        }
        if (nextLocation <= records.length) {
            records[nextLocation++] = record;
        }
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int capacity() {
        return records.length;
    }

    @Override
    public int size() {
        if (!lock.isHeldByCurrentThread()) {
            RuntimeException e = new LockIsNotAcquiredException("Lock is not acquired on page " + name);
            e.printStackTrace();
            throw e;
        }
        return nextLocation;
    }

    @Override
    public boolean isFull() {
        if (!lock.isHeldByCurrentThread()) {
            RuntimeException e = new LockIsNotAcquiredException("Lock is not acquired on page " + name);
            e.printStackTrace();
            throw e;
        }
        return size() >= records.length;
    }

    @Override
    public boolean isEmpty() {
        if (!lock.isHeldByCurrentThread()) {
            RuntimeException e = new LockIsNotAcquiredException("Lock is not acquired on page " + name);
            e.printStackTrace();
            throw e;
        }
        return size() == 0;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < nextLocation; ++i) {
            builder.append(records[i]);
            builder.append("\n");
        }
        return builder.toString();
    }
}
