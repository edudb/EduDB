/*
EduDB is made available under the OSI-approved MIT license.

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

package net.edudb.client.console.executor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.regex.Matcher;
import net.edudb.client.ClientWriter;
import net.edudb.engine.Utility;

public class CopyExecutor implements ConsoleExecutorChain {
	private ConsoleExecutorChain nextElement;
	private String regex = "\\A(?:(?i)copy)\\s+(\\D\\w*)\\s+(?:(?i)from)\\s+\\'(.+)\\'\\s+(?:(?i)delimiter)\\s+\\'(.+)\\'\\s*\\;?\\z";

	@Override
	public void setNextInChain(ConsoleExecutorChain chainElement) {
		this.nextElement = chainElement;
	}

	@Override
	public void execute(String string) {
		if (string.toLowerCase().startsWith("copy")) {
			Matcher matcher = Utility.getMatcher(string, regex);
			if (matcher.matches()) {
				String copyCommand = "copy " + matcher.group(1) + " delimiter '" + matcher.group(3) + "'" + "\r\n";

				String path = matcher.group(2);
				try {
					List<String> lines = Files.readAllLines(Paths.get(path));
					for (int i = 0; i < lines.size(); i++) {
						copyCommand += lines.get(i) + "\r\n";
					}
				} catch (IOException e) {
					e.printStackTrace();
				}

				ClientWriter.getInstance().write(copyCommand);
				return;
			}
		}
		nextElement.execute(string);
	}

}
