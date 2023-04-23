/*
 *
 * EduDB is made available under the OSI-approved MIT license.
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 * /
 */

package net.edudb.page;

import net.edudb.buffer.BufferManager;
import net.edudb.engine.Config;
import net.edudb.engine.FileManager;
import net.edudb.structure.Record;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * A structure that manages pages.
 *
 * @author Ahmed Abdul Badie
 */
public class PageManager implements Pageable, Serializable {

    private static final long serialVersionUID = -6801103344946561955L;

    /**
     * List of page names the manager is responsible for.
     */
    private final Map<String, Map<String, ArrayList<String>>> pageNames;

    public PageManager() {
        this.pageNames = new HashMap<>();
    }

    private void addWorkspaceAndDatabaseIfNotPresent(String workspaceName, String databaseName) {
        pageNames.putIfAbsent(workspaceName, new HashMap<>());
        pageNames.get(workspaceName).putIfAbsent(databaseName, new ArrayList<>());
    }

    /**
     * @deprecated Use {@link #getPageNames(String, String)} instead.
     */
    @Deprecated
    public ArrayList<String> getPageNames() {
        return getPageNames(Config.getCurrentWorkspace(), Config.getCurrentDatabaseName());
    }

    @Override
    public ArrayList<String> getPageNames(String workspaceName, String databaseName) {
        addWorkspaceAndDatabaseIfNotPresent(workspaceName, databaseName);
        return pageNames.get(workspaceName).get(databaseName);
    }

    @Override
    public void addPageName(String workspaceName, String databaseName, String pageName) {
        addWorkspaceAndDatabaseIfNotPresent(workspaceName, databaseName);
        pageNames.get(workspaceName).get(databaseName).add(pageName);
    }

    /**
     * @deprecated Use {@link #deletePages(String, String)} instead.
     */
    @Deprecated
    public void deletePages() {
        deletePages(Config.getCurrentWorkspace(), Config.getCurrentDatabaseName());
    }

    @Override
    public void deletePages(String workspaceName, String databaseName) {
        addWorkspaceAndDatabaseIfNotPresent(workspaceName, databaseName);
        for (String pageName : pageNames.get(workspaceName).get(databaseName)) {
            FileManager.getInstance().deletePage(workspaceName, databaseName, pageName);
        }
        pageNames.get(workspaceName).get(databaseName).clear();
    }

    private synchronized Page createPage(String workspaceName, String databaseName) {
        addWorkspaceAndDatabaseIfNotPresent(workspaceName, databaseName);

        PageFactory pageFactory = new PageFactory();
        Page page = pageFactory.makePage(Config.blockType());
        pageNames.get(workspaceName).get(databaseName).add(page.getName());

        return page;
    }

    /**
     * Adds a record to the last page.
     *
     * @param record Record to be added to a page.
     * @deprecated Use {@link #addRecord(String, String, Record)} instead.
     */
    @Deprecated
    public synchronized void addRecord(Record record) {
        addRecord(Config.getCurrentWorkspace(), Config.getCurrentDatabaseName(), record);
    }

    public synchronized void addRecord(String workspaceName, String databaseName, Record record) {
        addWorkspaceAndDatabaseIfNotPresent(workspaceName, databaseName);

        Page page;
        if (pageNames.get(workspaceName).get(databaseName).isEmpty()) {
            Page newPage = createPage(workspaceName, databaseName);
            BufferManager.getInstance().write(workspaceName, databaseName, newPage);
        }

        String pageName = pageNames.get(workspaceName).get(databaseName).get(pageNames.get(workspaceName).get(databaseName).size() - 1);
        page = BufferManager.getInstance().read(workspaceName, databaseName, pageName);

        page.open();
        page.addRecord(record);
        if (page.isFull()) {
            Page newPage = createPage(workspaceName, databaseName);
            BufferManager.getInstance().write(workspaceName, databaseName, newPage);
        }
        page.close();
    }

    /**
     * @deprecated Use {@link #print(String, String)} instead.
     */
    @Deprecated
    public void print() {
        print(Config.getCurrentWorkspace(), Config.getCurrentDatabaseName());
    }

    public void print(String workspaceName, String databaseName) {
        addWorkspaceAndDatabaseIfNotPresent(workspaceName, databaseName);

        for (String pageName : pageNames.get(workspaceName).get(databaseName)) {
            Page page = BufferManager.getInstance().read(workspaceName, databaseName, pageName);
            page.print();
        }
    }

}
