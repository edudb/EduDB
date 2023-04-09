/*
 *
 * EduDB is made available under the OSI-approved MIT license.
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 * /
 */

package net.edudb.buffer;

import net.edudb.engine.Config;
import net.edudb.engine.FileManager;
import net.edudb.page.Page;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Singleton that handles pages read/written from/to disk.
 *
 * @author Ahmed Abdul Badie
 */
public class BufferManager {

    private static final BufferManager instance = new BufferManager();
    // workspaceName -> databaseName -> pageName -> page
    private Map<String, Map<String, LinkedHashMap<String, Page>>> pageBuffer;
    private final Map<String, Map<String, PageReplacement>> replacement;

    private BufferManager() {
        pageBuffer = new HashMap<>();
        this.replacement = new HashMap<>();
    }

    public static BufferManager getInstance() {
        return instance;
    }

    private void addWorkspaceAndDatabaseIfNotPresent(String workspaceName, String databaseName) {
        pageBuffer.putIfAbsent(workspaceName, new HashMap<>());
        pageBuffer.get(workspaceName).putIfAbsent(databaseName, new LinkedHashMap<>());
        replacement.putIfAbsent(workspaceName, new HashMap<>());
        replacement.get(workspaceName).putIfAbsent(databaseName,
                new LRUPageReplacement(pageBuffer.get(workspaceName).get(databaseName)));
    }

    /**
     * Reads a page from the disk and adds it to the buffer if not present in
     * the buffer. If the page is inside the buffer pool, it is directly
     * returned.
     *
     * @param pageName Name of page to read.
     * @return Page The read page.
     * @deprecated Use {@link #read(String, String, String)} instead.
     */
    @Deprecated
    public synchronized Page read(String pageName) {
        return read(Config.getCurrentWorkspace(), Config.getCurrentDatabaseName(), pageName);
    }

    /**
     * Reads a page from the disk and adds it to the buffer if not present in
     * the buffer. If the page is inside the buffer pool, it is directly
     * returned.
     *
     * @param workspaceName The name of the workspace to read from.
     * @param databaseName  The name of the database to read from.
     * @param pageName      The name of the page to read.
     * @return The page read from disk.
     */
    public synchronized Page read(String workspaceName, String databaseName, String pageName) {
        addWorkspaceAndDatabaseIfNotPresent(workspaceName, databaseName);

        Page page = replacement.get(workspaceName).get(databaseName).read(pageName);

        if (page != null) {
            return page;
        }

        page = this.readFromDisk(workspaceName, databaseName, pageName);

        if (page != null) {
            replacement.get(workspaceName).get(databaseName).put(page);
        }
        return page;
    }

    /**
     * Writes a page to disk and adds it to the buffer.
     *
     * @param page The page to write.
     * @deprecated Use {@link #write(String, String, Page)} instead.
     */
    @Deprecated
    public synchronized void write(Page page) {
        write(Config.getCurrentWorkspace(), Config.getCurrentDatabaseName(), page);
    }

    /**
     * Writes a page to disk and adds it to the buffer.
     *
     * @param workspaceName The name of the workspace to write to.
     * @param databaseName  The name of the database to write to.
     * @param page          The page to write.
     */
    public synchronized void write(String workspaceName, String databaseName, Page page) {
        addWorkspaceAndDatabaseIfNotPresent(workspaceName, databaseName);

        replacement.get(workspaceName).get(databaseName).put(page);
    }

    /**
     * Writes all the pages from all workspaces and all databases to disk.
     * Used as a background task runs periodically.
     */
    public void writeAll() {
        for (String workspaceName : pageBuffer.keySet()) {
            writeAll(workspaceName);
        }
    }

    /**
     * @param workspaceName The name of the workspace to write all pages from.
     */
    public void writeAll(String workspaceName) {
        for (String databaseName : pageBuffer.get(workspaceName).keySet()) {
            writeAll(workspaceName, databaseName);
        }
    }

    /**
     * @param workspaceName The name of the workspace to write all pages from.
     * @param databaseName  The name of the database to write all pages from.
     */
    public void writeAll(String workspaceName, String databaseName) {
        addWorkspaceAndDatabaseIfNotPresent(workspaceName, databaseName);

        for (Page page : pageBuffer.get(workspaceName).get(databaseName).values()) {
            this.writeToDisk(workspaceName, databaseName, page);
        }

        pageBuffer.get(workspaceName).get(databaseName).clear();
    }

    /**
     * Reads a page from disk by requesting the read from the
     * {@link FileManager}.
     *
     * @param pageName The name of the page to be read from disk.
     * @return The page read from disk.
     */
    private Page readFromDisk(String workspaceName, String databaseName, String pageName) {
        return FileManager.getInstance().readPage(workspaceName, databaseName, pageName);
    }

    /**
     * Writes a page to disk by requesting the write from the
     * {@link FileManager}.
     *
     * @param workspaceName
     * @param page          The page to be written to disk.
     */
    private void writeToDisk(String workspaceName, String databaseName, Page page) {
        FileManager.getInstance().writePage(workspaceName, databaseName, page);
    }

    public void removeWorkspace(String workspaceName) {
        pageBuffer.remove(workspaceName);
        replacement.remove(workspaceName);
    }

    public void removeDatabase(String workspaceName, String databaseName) {
        pageBuffer.get(workspaceName).remove(databaseName);
        replacement.get(workspaceName).remove(databaseName);
    }

    public Map<String, Map<String, LinkedHashMap<String, Page>>> getPageBuffer() {
        return pageBuffer;
    }

    public Map<String, Map<String, PageReplacement>> getReplacement() {
        return replacement;
    }
}
