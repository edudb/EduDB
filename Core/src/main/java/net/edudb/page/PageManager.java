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
        for (String pageName : pageNames) {
            FileManager.getInstance().deletePage(Config.getCurrentWorkspace(), Config.getCurrentDatabaseName(), pageName);
        }
        pageNames.clear();
    }

    private synchronized Page createPage() {
        PageFactory pageFactory = new PageFactory();
        Page page = pageFactory.makePage(Config.blockType());
        pageNames.add(page.getName());

        return page;
    }

    public synchronized String addRecord(Record record) {
        String workspaceName = Config.getCurrentWorkspace();
        String databaseName = Config.getCurrentDatabaseName();
        Page page;
        if (pageNames.isEmpty()) {
            Page newPage = createPage();
            BufferManager.getInstance().write(workspaceName, databaseName, newPage);
        }

        String pageName = pageNames.get(pageNames.size() - 1);
        page = BufferManager.getInstance().read(workspaceName, databaseName, pageName);

//        page.open();
        page.addRecord(record);
        if (page.isFull()) {
            Page newPage = createPage();
            BufferManager.getInstance().write(workspaceName, databaseName, newPage);
        }
//        page.close();
        return page.getName();

    }

    public void print() {
        for (String pageName : pageNames) {
            Page page = BufferManager.getInstance().read(Config.getCurrentWorkspace(), Config.getCurrentDatabaseName(), pageName);
            System.out.println(page);
        }
    }
}
