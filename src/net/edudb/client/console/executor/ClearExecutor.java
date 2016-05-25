/*
EduDB is made available under the OSI-approved MIT license.

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

package net.edudb.client.console.executor;

import net.edudb.client.Client;
import net.edudb.client.console.Console;

public class ClearExecutor implements ConsoleExecutorChain {
	private ConsoleExecutorChain nextChainElement;

	@Override
	public void setNextInChain(ConsoleExecutorChain chainElement) {
		this.nextChainElement = chainElement;
	}

	@Override
	public void execute(String string) {
		if (string.equalsIgnoreCase("clear")) {
			/**
			 * JLine prints the prompt twice when clearing the screen and
			 * reading a new line.
			 *
			 * Setting the prompt to "" and then resetting it solves the
			 * problem.
			 *
			 * This is a temporary fix until it is fixed, it not yet.
			 * 
			 */
			Console.getInstance().setPrompt("");
			Console.getInstance().clearScreen();
			Console.getInstance().flush();
			Console.getInstance().setPrompt("edudb$ ");
			Client.getInstance().getHandler().setReceiving(false);
			return;
		}
		nextChainElement.execute(string);
	}
}
