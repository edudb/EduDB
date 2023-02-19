/*
EduDB is made available under the OSI-approved MIT license.

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

package net.edudb.console;

import java.io.IOException;
import jline.console.ConsoleReader;
import net.edudb.console.executor.*;

/**
 * Singleton that handles the console and its commands.
 * 
 * @author Ahmed Abdul Badie
 *
 */
public class DatabaseConsole {

	private static DatabaseConsole instance = new DatabaseConsole();
	private ConsoleReader consoleReader;

	private DatabaseConsole() {
		try {
			this.consoleReader = new ConsoleReader();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static DatabaseConsole getInstance() {
		return instance;
	}

	/**
	 * Reads a line from the console.
	 * 
	 * @return The read line.
	 */
	public String readLine() {
		try {
			return consoleReader.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Sets the console prompt.
	 * 
	 * @param prompt
	 *            The prompt to set.
	 */
	public void setPrompt(String prompt) {
		consoleReader.setPrompt(prompt);
	}

	/**
	 * Clears the console screen.
	 */
	public void clearScreen() {
		try {
			consoleReader.clearScreen();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void flush() {
		try {
			consoleReader.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Prints EduDB's supported commands.
	 */
	public void printHelp() {
		String supportedCommands[] = new String[] { "\tclear", "\texit" };
		this.writeln("Supported commands:");
		for (String string : supportedCommands) {
			this.writeln(string);
		}
	}

	/**
	 * Writes to the system's console.
	 * 
	 * @param object
	 *            Object to write.
	 */
	public void write(Object object) {
		System.out.print(object);
	}

	/**
	 * Writes a line to the system's console.
	 * 
	 * @param object
	 *            Object to write.
	 */
	public void writeln(Object object) {
		System.out.println(object);
	}

	/**
	 * Starts an instance of the console.
	 */
	public void start() {

		ConsoleExecutorChain chain = DatabaseConsole.getExecutionChain();

		String line;
		try {
			while ((line = consoleReader.readLine()) != null) {
				chain.execute(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * Connects the elements of the given chain in their respected order.
	 * 
	 * @param chainElements
	 *            Elements of the chain to connect
	 * @return The first element of the connected chain.
	 */
	public static ConsoleExecutorChain connectChain(ConsoleExecutorChain[] chainElements) {
		for (int i = 0; i < chainElements.length - 1; i++) {
			chainElements[i].setNextElementInChain(chainElements[i + 1]);
		}
		return chainElements[0];
	}

	/**
	 * Sets the console's execution chain.
	 * 
	 * @return Console's execution chain.
	 */
	public static ConsoleExecutorChain getExecutionChain() {
		ConsoleExecutorChain clear = new ClearExecutor();
		ConsoleExecutorChain exit = new ExitExecutor();
		ConsoleExecutorChain help = new HelpExecutor();
		ConsoleExecutorChain copy = new CopyExecutor();
		ConsoleExecutorChain open = new OpenDatabaseExecutor();
		ConsoleExecutorChain close = new CloseDatabaseExecutor();
		ConsoleExecutorChain dropTable = new DropTableExecutor();
		ConsoleExecutorChain create = new CreateDatabaseExecutor();
		ConsoleExecutorChain drop = new DropDatabaseExecutor();
		ConsoleExecutorChain sql = new SQLExecutor();

		return DatabaseConsole.connectChain(new ConsoleExecutorChain[] { clear, exit, help, copy, open, close,
				dropTable, create, drop, sql });
	}

}
