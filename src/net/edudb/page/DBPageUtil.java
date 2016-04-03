/*
EduDB is made available under the OSI-approved MIT license.

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

package net.edudb.page;

import net.edudb.engine.DBBufferManager;
import net.edudb.server.ServerWriter;
import net.edudb.statistics.Schema;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public class DBPageUtil {
	private static HashMap<String, DBPageID> tables;
	private static boolean initialized;

	public static DBPageID getPageID(String tableName) {
		init();
		DBPageID id = tables.get(tableName);
		if (id != null)
			return id;
		ServerWriter.getInstance().writeln("table " + tableName + " does not exist");
		return null;
	}

	private static void init() {
		if (!initialized) {
			initialized = true;
			Set<String> tableNames = Schema.getTableNames();
			tables = new HashMap<>();
//			BufferManager manager = TransactionManager.getBufferManager();
			Iterator iter = tableNames.iterator();
			HashMap<DBPageID, DBPage> empty = new HashMap<>();
			while (iter.hasNext()) {
				String name = (String) iter.next();
				DBPage page = new DBPage(name);
				DBPageID id = new DBPageID();
				page.setPageID(id);
				tables.put(name, id);
				empty.put(id, page);
			}
//			manager.initEmpty(empty);
			DBBufferManager.getInstance().initEmpty(empty);
		}
	}
}
