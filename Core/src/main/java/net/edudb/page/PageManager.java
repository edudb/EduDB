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

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * A structure that manages pages.
 *
 * @author Ahmed Abdul Badie
 */
public class PageManager implements Pageable, Serializable {

    @Serial
    private static final long serialVersionUID = -6801103344946561955L;
    private final ArrayList<String> pageNames;

    public PageManager() {
        this.pageNames = new ArrayList<>();
    }

    @Override
    public ArrayList<String> getPageNames() {
        return pageNames;
    }

    @Override
    public void addPageName(String pageName) {
        pageNames.add(pageName);
    }

    @Override
    public void deletePages() {
        // todo: acquire a lock on the page
        for (String pageName : pageNames) {
            FileManager.getInstance().deletePage(Config.getCurrentWorkspace(), Config.getCurrentDatabaseName(), pageName);
        }
        pageNames.clear();
    }

    public synchronized String addRecord(Record record) {
        if (pageNames.isEmpty()) createPage();

        String lastPageName = pageNames.get(pageNames.size() - 1);
        Page lastPage = readPage(lastPageName);
        lastPage.acquireLock();

        if (lastPage.isFull()) {
            lastPage.releaseLock();
            lastPage = createPage();
            lastPage.acquireLock();
        }

        lastPage.addRecord(record);
        lastPage.releaseLock();
        return lastPage.getName();
    }

    private synchronized Page createPage() {
        PageFactory pageFactory = new PageFactory();
        Page page = pageFactory.makePage(Config.blockType());
        pageNames.add(page.getName());
        BufferManager.getInstance().write(Config.getCurrentWorkspace(), Config.getCurrentDatabaseName(), page);
        return page;
    }

    private synchronized Page readPage(String pageName) {
        return BufferManager.getInstance().read(Config.getCurrentWorkspace(), Config.getCurrentDatabaseName(), pageName);
    }

    public void print() {
        for (String pageName : pageNames) {
            Page page = BufferManager.getInstance().read(Config.getCurrentWorkspace(), Config.getCurrentDatabaseName(), pageName);
            System.out.println(page);
        }
    }
}
