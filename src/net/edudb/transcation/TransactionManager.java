/*
EduDB is made available under the OSI-approved MIT license.

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

package net.edudb.transcation;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;

import net.edudb.exception.DBAppException;
import net.edudb.exception.DBEngineException;
import net.edudb.operator.Operator;
import net.edudb.operator.UpdateOperator;
//import net.edudb.operator.UpdateOperator;
import net.edudb.server.ServerWriter;

public class TransactionManager {

	private static BufferManager bufferManager;

	public static void init(BufferManager manager) {
		bufferManager = manager;
	}

	public static BufferManager getBufferManager() {
		return bufferManager;
	}

	public static void run(Operator op) {
		if (op instanceof UpdateOperator) {
			updateTable((UpdateOperator) op);
			ServerWriter.getInstance().writeln("DBTransactionManager (run): " + "#1");
		} else {
			ServerWriter.getInstance().writeln("DBTransactionManager (run): " + "#2");
			op.print();
		}
	}

	public void createTable(String strTableName, Hashtable<String, String> htblColNameType,
			Hashtable<String, String> htblColNameRefs, String strKeyColName) throws DBAppException {
	}

	public void createIndex(String strTableName, String strColName) throws DBAppException {

	}

	public void insertIntoTable(String strTableName, Hashtable<String, String> htblColNameValue) throws DBAppException {

	}

	public static void updateTable(UpdateOperator updateOperator) {
		ArrayList<Step> steps = updateOperator.getSteps();
		Transaction transaction = new Transaction();
		transaction.init(steps);
		Thread thread = new Thread(transaction);
		thread.run();
	}

	public void deleteFromTable(String strTableName, Hashtable<String, String> htblColNameValue, String strOperator)
			throws DBAppException {

	}

	public Iterator selectFromTable(String strTable, Hashtable<String, String> htblColNameValue, String strOperator)
			throws DBAppException {

		return null;
	}

	public void saveAll() throws DBEngineException {

	}
}
