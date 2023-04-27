/*
 *
 * EduDB is made available under the OSI-approved MIT license.
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 * /
 */

package net.edudb.index;

import btree4j.BTreeCallback;
import btree4j.BTreeException;
import btree4j.BTreeIndex;
import btree4j.Value;
import btree4j.indexer.BasicIndexQuery;
import net.edudb.data_type.DataType;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

public class BtreeIndex<T extends DataType> implements Index<T> {
    private final BTreeIndex index;

    public BtreeIndex(File indexFile) {
        try {
            this.index = new BTreeIndex(indexFile);
            this.index.init(false);
        } catch (BTreeException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void insert(DataType key, String pageName) {
        Value k = new Value(key.toString());
        Value v = new Value(pageName);
        try {
            index.addValue(k, v);
        } catch (BTreeException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(DataType key) {
        Value k = new Value(key.toString());
        try {
            this.index.remove(k);
        } catch (BTreeException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Set<String> search(DataType key) {
        Value k = new Value(key.toString());
        final Set<String> pages = new HashSet<>();
        try {
            this.index.search(new BasicIndexQuery.IndexConditionEQ(k), new BTreeCallback() {
                public boolean indexInfo(Value value, long pointer) {
                    throw new UnsupportedOperationException();
                }

                public boolean indexInfo(Value key, byte[] value) {
                    String vv = new Value(value).toString();
                    pages.add(vv);
                    return true;
                }
            });
        } catch (BTreeException e) {
            throw new RuntimeException(e);
        }
        return pages;
    }
}
