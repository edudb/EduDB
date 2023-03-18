/*
 *
 * EduDB is made available under the OSI-approved MIT license.
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 * /
 */

package net.edudb.index.PartitionedHashTable.Tests;

import net.edudb.index.PartitionedHashTable.EBDirectory;
import net.edudb.index.PartitionedHashTable.EBIndex;
import org.junit.Ignore;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

/**
 * @author Ahmed Abdul Badie
 */

public class EBDirectoryTest {

    @Test
    public void initTest() {
        EBDirectory directory = new EBDirectory(16, 2, 5);

        assertEquals(16, directory.length());
    }

    @Test
    public void addIndexTest() {
        EBDirectory directory = new EBDirectory(16, 2, 5);
        EBIndex index = new EBIndex(new String[]{"a", "b"}, "page", 0);
        directory.addIndex(index);

        ArrayList<EBIndex> indexes = directory.getIndex(index);

        assertEquals(1, indexes.size());
    }

    @Ignore
    public void updateIndexTest() {
        EBDirectory directory = new EBDirectory(16, 2, 5);
        EBIndex oldIndex = new EBIndex(new String[]{"a", "c"}, "page", 0);
        EBIndex newIndex = new EBIndex(new String[]{"d", null}, "page", 0);


        directory.addIndex(oldIndex);
        directory.updateIndex(oldIndex, newIndex);

        ArrayList<EBIndex> indexes = directory.getIndex(oldIndex);

//		System.out.println(indexes.get(0));

        assertArrayEquals(new String[]{"d", "c"}, indexes.get(0).getValues());
    }

    @Test
    public void getIndexTest() {
        EBDirectory directory = new EBDirectory(16, 2, 5);
        EBIndex index = new EBIndex(new String[]{"a", "b"}, "page", 0);


        directory.addIndex(index);

        ArrayList<EBIndex> indexes = directory.getIndex(index);

        assertEquals(index, indexes.get(0));
    }

    @Test
    public void getIndexesTest() {
        EBDirectory directory = new EBDirectory(16, 2, 5);
        for (int i = 0; i < 14; i++) {
            directory.addIndex(new EBIndex(new String[]{"a", i + ""}, "page", i));
        }

        assertEquals(14, directory.getIndex(new EBIndex(new String[]{"a", null})).size());
    }

    @Test
    public void deleteIndexTest() {
        EBDirectory directory = new EBDirectory(16, 2, 5);
        EBIndex index = new EBIndex(new String[]{"a", "b"}, "page", 0);
        directory.addIndex(index);

        directory.deleteIndex(index);

        ArrayList<EBIndex> indexes = directory.getIndex(index);

        assertEquals(0, indexes.size());
    }

}
