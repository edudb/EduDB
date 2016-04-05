/*
EduDB is made available under the OSI-approved MIT license.

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

package net.edudb.engine;

import java.io.IOException;

//import sun.org.mozilla.javascript.Synchronizer;

//import javax.print.DocFlavor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.edudb.block.BlockAbstractFactory;
import net.edudb.block.BlockWriter;
import net.edudb.block.BlockWriterFactory;
import net.edudb.page.DBPage;
import net.edudb.page.DBPageID;
import net.edudb.page.Page;
import net.edudb.server.ServerWriter;
import net.edudb.transcation.DBConfig;

/**
 * Created by mohamed on 5/20/14.
 */
public class DBBufferManager {

	private static DBBufferManager instance = new DBBufferManager();

	HashMap<DBPageID, DBPage> used;
	HashMap<DBPageID, DBPage> empty;
	HashMap<DBPageID, DBPage.LockState> locks;
	HashMap<DBPageID, ArrayList<Thread>> listeners;
	HashMap<DBPageID, Integer> readersCount;
	HashMap<DBPageID, DBPage.PageState> states;

	private DBBufferManager() {
		init();
	}

	public static DBBufferManager getInstance() {
		return instance;
	}

	public void init() {
		used = new HashMap<>();
		empty = new HashMap<>();
		locks = new HashMap<>();
		listeners = new HashMap<>();
		readersCount = new HashMap<>();
		states = new HashMap<>();
	}

	public synchronized DBPage read(DBPageID pageID, boolean bModify) {
		DBPage page = null;
		if (used.containsKey(pageID)) {
			ServerWriter.getInstance().writeln("found" + (bModify ? " write" : " read"));
			page = used.get(pageID);
			page = page.getCopy();
			if (bModify) {
				if (locks.get(pageID) != DBPage.LockState.free) {
					listeners.get(pageID).add(Thread.currentThread());
					return null;
				}
				locks.put(pageID, DBPage.LockState.write);
				page.setLastAccessed();
				return page;
			}
			if (locks.get(pageID) == DBPage.LockState.write) {
				listeners.get(pageID).add(Thread.currentThread());
				readersCount.put(pageID, readersCount.get(pageID) + 1);
				return null;
			}
			locks.put(pageID, DBPage.LockState.read);
			page.setLastAccessed();
			readersCount.put(pageID, readersCount.get(pageID) + 1);
			// return page;
		} else {
			ServerWriter.getInstance().writeln("not found");
			page = empty.get(pageID);
			ServerWriter.getInstance().writeln("BufferManager (read)-Empty: " + page);
			if (used.size() == DBConfig.getMaximumUsedBufferSlots()) {
				ServerWriter.getInstance().writeln("what ");
				removeLRU();
			}
			allocate(pageID, page);
			if (bModify) {
				locks.put(pageID, DBPage.LockState.write);
			} else {
				locks.put(pageID, DBPage.LockState.read);
				readersCount.put(pageID, readersCount.get(pageID) + 1);
			}
			page.setLastAccessed();
			// return page;
		}
		return page;
	}

	private void allocate(DBPageID pageId, DBPage page) {
		ServerWriter.getInstance().writeln("buf" + page);
		page.allocate();
		used.put(pageId, page);
		locks.put(pageId, DBPage.LockState.free);
		listeners.put(pageId, new ArrayList<Thread>());
		readersCount.put(pageId, 0);
		states.put(pageId, DBPage.PageState.clean);
		empty.remove(pageId);
	}

	public synchronized void write(DBPageID pageID, DBPage page) {
		used.put(pageID, page);
		states.put(pageID, DBPage.PageState.dirty);
		releasePage(pageID);
	}

	public synchronized void releasePage(DBPageID pageID) {
		readersCount.put(pageID, readersCount.get(pageID) - 1);
		if (locks.get(pageID) == DBPage.LockState.write
				|| (locks.get(pageID) == DBPage.LockState.read && readersCount.get(pageID) == 0)) {
			locks.put(pageID, DBPage.LockState.free);
			ArrayList<Thread> threads = listeners.get(pageID);
			for (Thread t : threads) {
				synchronized (t) {
					ServerWriter.getInstance().writeln(t.getName());
					t.notify();
				}
			}
			listeners.put(pageID, new ArrayList<Thread>());

		}
	}

	public void initEmpty(HashMap<DBPageID, DBPage> empty) {
		this.empty = empty;
	}

	// class LRUThreaad implements Runnable {
	// @Override
	// public void run() {
	// removeLRU();
	// }
	// }

	private synchronized boolean removeLRU() {
		ServerWriter.getInstance().writeln("lru " + used.size());
		DBPage toBeReplaced = null;
		int min = Integer.MAX_VALUE;
		// int minIndex = -1;
		DBPageID minId = null;
		Iterator iter = used.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry pair = (Map.Entry) iter.next();
			DBPage page1 = (DBPage) pair.getValue();
			ServerWriter.getInstance().writeln(locks);
			ServerWriter.getInstance().writeln(page1.getPageId());
			if (locks.get(page1.getPageId()) == DBPage.LockState.free && page1.getlastAccessed() < min) {
				toBeReplaced = page1;
				min = page1.getlastAccessed();
				minId = page1.getPageId();
			}
		}
		if (minId != null) {
			used.remove(minId);
			locks.remove(minId);
			readersCount.remove(minId);
			listeners.remove(minId);
			empty.put(minId, toBeReplaced);
			if (states.get(toBeReplaced.getPageId()) == DBPage.PageState.dirty) {
				BlockAbstractFactory blockWriterFactory = new BlockWriterFactory();
				BlockWriter blockWriter = blockWriterFactory.getWriter(Config.blockType());
				try {
					blockWriter.write((Page) toBeReplaced);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				// toBeReplaced.write();
			}
			toBeReplaced.free();
			ServerWriter.getInstance().writeln("lru " + used.size());
			return true;
		}
		ServerWriter.getInstance().writeln("lru " + used.size());
		return false;
	}
}
