/*
 *
 * EduDB is made available under the OSI-approved MIT license.
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 * /
 */

package net.edudb.index;

import net.edudb.data_type.VarCharType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class BtreeIndexTest {
    @TempDir
    static File tempDir;
    private static File indexFile;


    @BeforeEach
    void setUp() {
        indexFile = new File(tempDir, "BIndexFileTest.idx");
    }

    @Test
    @DisplayName("should inserts a key and a page name into the index")
    void insert() {
        Index<VarCharType> btreeIndex = new BtreeIndex<>(indexFile);
        VarCharType key = new VarCharType("key");
        String pageName = "pageName1";

        btreeIndex.insert(key, pageName);
        Set<String> pages = btreeIndex.search(key);

        assertThat(pages).hasSize(1).containsExactly(pageName);
    }

    @Test
    @DisplayName("should delete all occurrences of key and a page name from the index")
    void delete1() {
        Index<VarCharType> btreeIndex = new BtreeIndex<>(indexFile);
        VarCharType key = new VarCharType("key");
        String pageName = "pageName";

        btreeIndex.insert(key, pageName);
        btreeIndex.insert(key, pageName);
        btreeIndex.delete(key);
        Set<String> pages = btreeIndex.search(key);

        assertThat(pages).isEmpty();
    }

    @Test
    @DisplayName("should delete specific key and a page name from the index")
    void delete2() {
        Index<VarCharType> btreeIndex = new BtreeIndex<>(indexFile);
        VarCharType key1 = new VarCharType("key1");
        String pageName1 = "pageName1";
        VarCharType key2 = new VarCharType("key2");
        String pageName2 = "pageName2";

        btreeIndex.insert(key1, pageName1);
        btreeIndex.insert(key2, pageName2);
        btreeIndex.delete(key2);
        Set<String> pages1 = btreeIndex.search(key1);
        Set<String> pages2 = btreeIndex.search(key2);

        assertThat(pages1).hasSize(1).containsExactly(pageName1);
        assertThat(pages2).isEmpty();
    }

    @Test
    @DisplayName("should search for all occurrences of key and return the page names")
    void search1() {
        Index<VarCharType> btreeIndex = new BtreeIndex<>(indexFile);
        VarCharType key = new VarCharType("key");
        String pageName1 = "pageName1";
        String pageName2 = "pageName2";

        btreeIndex.insert(key, pageName1);
        btreeIndex.insert(key, pageName2);
        Set<String> pages = btreeIndex.search(key);

        assertThat(pages).hasSize(2).containsExactly(pageName1, pageName2);
    }

    @Test
    @DisplayName("should search for specific of key and return the page names")
    void search2() {
        Index<VarCharType> btreeIndex = new BtreeIndex<>(indexFile);
        VarCharType key1 = new VarCharType("key1");
        String pageName1 = "pageName1";
        VarCharType key2 = new VarCharType("key2");
        String pageName2 = "pageName2";

        btreeIndex.insert(key1, pageName1);
        btreeIndex.insert(key2, pageName2);
        Set<String> pages = btreeIndex.search(key1);

        assertThat(pages).hasSize(1).containsExactly(pageName1);
    }
}
