/*
EduDB is made available under the OSI-approved MIT license.

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

package net.edudb.engine;

//import sun.org.mozilla.javascript.Synchronizer;

//import javax.print.DocFlavor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.edudb.page.Page;
import net.edudb.page.PageID;
import net.edudb.server.ServerWriter;
import net.edudb.transcation.DBConfig;

/**
 * Created by mohamed on 5/20/14.
 */
public class BufferManager {
	
	private static BufferManager instance = new BufferManager();

	HashMap<PageID, Page> used;
	HashMap<PageID, Page> empty;
	HashMap<PageID, Page.LockState> locks;
	HashMap<PageID, ArrayList<Thread>> listeners;
	HashMap<PageID, Integer> readersCount;
	HashMap<PageID, Page.PageState> states;

	private BufferManager() {
		init();
	}
	
	public static BufferManager getInstance() {
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

	public synchronized Page read(PageID pageID, boolean bModify) {
		if (used.containsKey(pageID)) {
			ServerWriter.getInstance().writeln("found" + (bModify ? " write" : " read"));
			Page page1 = used.get(pageID);
			page1 = page1.getCopy();
			if (bModify) {
				if (locks.get(pageID) != Page.LockState.free) {
					listeners.get(pageID).add(Thread.currentThread());
					return null;
				}
				locks.put(pageID, Page.LockState.write);
				page1.setLastAccessed();
				return page1;
			}
			if (locks.get(pageID) == Page.LockState.write) {
				listeners.get(pageID).add(Thread.currentThread());
				readersCount.put(pageID, readersCount.get(pageID) + 1);
				return null;
			}
			locks.put(pageID, Page.LockState.read);
			page1.setLastAccessed();
			readersCount.put(pageID, readersCount.get(pageID) + 1);
			return page1;
		} else {
			ServerWriter.getInstance().writeln("not found");
			Page page1 = empty.get(pageID);
			if (used.size() == DBConfig.getMaximumUsedBufferSlots()) {
				ServerWriter.getInstance().writeln("what ");
				removeLRU();
			}
			allocate(pageID, page1);
			if (bModify) {
				locks.put(pageID, Page.LockState.write);
			} else {
				locks.put(pageID, Page.LockState.read);
				readersCount.put(pageID, readersCount.get(pageID) + 1);
			}
			page1.setLastAccessed();
			return page1;
		}
	}

	private void allocate(PageID pageId, Page page) {
		ServerWriter.getInstance().writeln("buf" + page);
		page.allocate();
		used.put(pageId, page);
		locks.put(pageId, Page.LockState.free);
		listeners.put(pageId, new ArrayList<Thread>());
		readersCount.put(pageId, 0);
		states.put(pageId, Page.PageState.clean);
		empty.remove(pageId);
	}

	public synchronized void write(PageID pageID, Page page) {
		used.put(pageID, page);
		states.put(pageID, Page.PageState.dirty);
		releasePage(pageID);
	}

	public synchronized void releasePage(PageID pageID) {
		readersCount.put(pageID, readersCount.get(pageID) - 1);
		if (locks.get(pageID) == Page.LockState.write
				|| (locks.get(pageID) == Page.LockState.read && readersCount.get(pageID) == 0)) {
			locks.put(pageID, Page.LockState.free);
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

	public void initEmpty(HashMap<PageID, Page> empty) {
		this.empty = empty;
	}

	class LRUThreaad implements Runnable {
		@Override
		public void run() {
			removeLRU();
		}
	}

	private synchronized boolean removeLRU() {
		ServerWriter.getInstance().writeln("lru " + used.size());
		Page toBeReplaced = null;
		int min = Integer.MAX_VALUE;
		int minIndex = -1;
		PageID minId = null;
		Iterator iter = used.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry pair = (Map.Entry) iter.next();
			Page page1 = (Page) pair.getValue();
			ServerWriter.getInstance().writeln(locks);
			ServerWriter.getInstance().writeln(page1.getPageId());
			if (locks.get(page1.getPageId()) == Page.LockState.free && page1.getlastAccessed() < min) {
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
			if (states.get(toBeReplaced.getPageId()) == Page.PageState.dirty) {
				toBeReplaced.write();
			}
			toBeReplaced.free();
			ServerWriter.getInstance().writeln("lru " + used.size());
			return true;
		}
		ServerWriter.getInstance().writeln("lru " + used.size());
		return false;
	}

	public static class Reader implements Runnable {

		private final PageID id;
		private final BufferManager manager;
		private int count;

		public Reader(PageID id, BufferManager manager, int count) {

			this.id = id;
			this.manager = manager;
			this.count = count;
		}

		@Override
		public void run() {
			ServerWriter.getInstance().writeln("reader" + count);
			manager.read(id, false);
			synchronized (this) {
				try {
					wait(2000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			manager.releasePage(id);
			ServerWriter.getInstance().writeln("reader" + count + " released");
		}
	}

	public static class Writer implements Runnable {

		private final PageID id;
		private final BufferManager manager;
		private int count;

		public Writer(PageID id, BufferManager manager, int count) {

			this.id = id;
			this.manager = manager;
			this.count = count;
		}

		@Override
		public void run() {
			ServerWriter.getInstance().writeln("writer" + count);
			Page read = manager.read(id, true);
			if (read == null) {
				try {
					Thread thread = Thread.currentThread();
					synchronized (thread) {
						ServerWriter.getInstance().writeln("going to sleep ");
						thread.wait();
						ServerWriter.getInstance().writeln("sleepy ");
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

			}
			synchronized (this) {
				try {
					wait(2000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			ServerWriter.getInstance().writeln("awake ");
			manager.releasePage(id);
			ServerWriter.getInstance().writeln("writer " + count + " released");
			// manager.write();
		}
	}

	public static void main(String[] args) {
		BufferManager manager = new BufferManager();
		manager.init();
		Page page1 = new Page();
		PageID id1 = new PageID();
		page1.setPageID(id1);
		manager.empty.put(id1, page1);
		Writer writer1 = new Writer(id1, manager, 1);
		Thread t3 = new Thread(writer1);
		t3.start();
		Reader reader1 = new Reader(id1, manager, 1);
		Thread t1 = new Thread(reader1);
		t1.start();
		Thread t2 = new Thread(reader1);
		t2.start();
		Thread t = Thread.currentThread();
		synchronized (t) {
			try {
				t.wait(3000);
			} catch (Exception e) {

			}
			ServerWriter.getInstance().writeln(manager.used.size());
			manager.removeLRU();
		}
	}
}
