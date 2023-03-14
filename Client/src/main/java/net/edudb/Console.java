/*
 *
 * EduDB is made available under the OSI-approved MIT license.
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 * /
 */

package net.edudb;

import org.jline.reader.EndOfFileException;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.UserInterruptException;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.jline.utils.InfoCmp;

import java.io.IOException;

public class Console {
    private Terminal terminal;
    private LineReader lineReader;
    private String prompt;


    public Console() {
        try {
            this.terminal = TerminalBuilder.terminal();
            this.lineReader = LineReaderBuilder.builder().terminal(terminal).build();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String readLine() throws UserInterruptException, EndOfFileException {
        return lineReader.readLine(this.prompt);
    }

    public String readLine(String prompt) throws UserInterruptException, EndOfFileException {
        return lineReader.readLine(prompt);
    }

    public String readPassword(String prompt) throws UserInterruptException, EndOfFileException {
        return lineReader.readLine(prompt, '*');
    }

    public void displayMessage(String message) {
        terminal.writer().println(message);
        terminal.flush();
    }

    public void clearConsole() {
        terminal.flush();
        terminal.puts(InfoCmp.Capability.clear_screen);
        terminal.flush();
    }

    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }

}
