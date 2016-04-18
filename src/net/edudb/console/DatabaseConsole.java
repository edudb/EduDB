/*
EduDB is made available under the OSI-approved MIT license.

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

package net.edudb.console;

import java.io.IOException;
import jline.console.ConsoleReader;
//import jline.console.CursorBuffer;

/**
 * Singleton that handles the console and its commands.
 * 
 * @author Ahmed Abdul Badie
 *
 */
public class DatabaseConsole {

	private static DatabaseConsole instance = new DatabaseConsole();
	private ConsoleReader consoleReader;

	/**
	 * See the last two methods
	 */
//	private CursorBuffer stashed;

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

	public String readLine() {
		try {
			return consoleReader.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public void setPrompt(String prompt) {
		consoleReader.setPrompt(prompt);
	}

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

	public void printHelp() {
		String supportedCommands[] = new String[] { "\tclear", "\texit" };
		this.writeln("Supported commands:");
		for (String string : supportedCommands) {
			this.writeln(string);
		}
	}

	public void write(Object object) {
		System.out.print(object);
	}

	public void writeln(Object object) {
		System.out.println(object);
	}

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

	public static ConsoleExecutorChain getExecutionChain() {
		ConsoleExecutorChain clear = new ClearExecutor();
		ConsoleExecutorChain exit = new ExitExecutor();
		ConsoleExecutorChain help = new HelpExecutor();
		ConsoleExecutorChain copy = new CopyExecutor();
		ConsoleExecutorChain sql = new SQLExecutor();
		ConsoleExecutorChain test = new ConcurrentTestExecutor();

		clear.setNextInChain(exit);
		exit.setNextInChain(help);
		help.setNextInChain(copy);
		copy.setNextInChain(test);
		test.setNextInChain(sql);
		sql.setNextInChain(new NullExecutor());

		return clear;
	}

	/**
	 * These methods are taken from a Stack Overflow thread.
	 * 
	 * http://stackoverflow.com/questions/9010099/jline-keep-prompt-at-the-
	 * bottom
	 * 
	 * These methods are supposed to handle the prompt when threads are running
	 * and are writing to the console.
	 */

//	private void stashLine() {
//		this.stashed = this.consoleReader.getCursorBuffer().copy();
//		// try {
//		// this.consoleReader.getOutput().write("\u001b[1G\u001b[K");
//		// this.consoleReader.flush();
//		// } catch (IOException e) {
//		// // ignore
//		// }
//	}
//
//	private void unstashLine() {
//		try {
//			this.consoleReader.resetPromptLine(this.consoleReader.getPrompt(), this.stashed.toString(),
//					this.stashed.cursor);
//		} catch (IOException e) {
//			// ignore
//		}
//	}

}
