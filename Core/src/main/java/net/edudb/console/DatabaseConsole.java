/*
 *
 * EduDB is made available under the OSI-approved MIT license.
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 * /
 */

package net.edudb.console;

import jline.console.ConsoleReader;
import jline.console.UserInterruptException;
import jline.console.completer.StringsCompleter;
import net.edudb.client.Client;
import net.edudb.client.ClientHandler;
import net.edudb.console.executor.*;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Singleton that handles the console and its commands.
 *
 * @author Ahmed Abdul Badie
 */
public class DatabaseConsole {

    private static DatabaseConsole instance = new DatabaseConsole();
    private ConsoleReader consoleReader;

    private DatabaseConsole() {
        try {
            this.consoleReader = new ConsoleReader();
            consoleReader.setHandleUserInterrupt(true);

            ArrayList<String> strings = new ArrayList<>();
            strings.add("clear");
            strings.add("close database");
            strings.add("copy");
            strings.add("create");
            strings.add("delete");
            strings.add("drop");
            strings.add("exit");
            strings.add("help");
            strings.add("insert into");
            strings.add("open database");
            strings.add("select");
            strings.add("update");
            this.consoleReader.addCompleter(new StringsCompleter(strings));
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
     * @param prompt The prompt to set.
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
        String supportedCommands[] = new String[]{"\tclear", "\tCLOSE DATABASE",
                "\tCOPY table_name FROM 'path' DELIMITER 'delimiter'", "\texit", "\tOPEN DATABASE database_name", "\tLIST DATABASES",
                "\tSQL commands:", "\t\tCREATE DATABASE database_name", "\t\tCREATE TABLE table_name (column_type_list)",
                "\t\tDELETE FROM table_name [WHERE condition]", "\t\tDROP DATABASE database_name",
                "\t\tINSERT INTO table_name VALUES (values)", "\t\tUPDATE table_name SET column_value_list [WHERE condition]"};
        this.writeln("Supported commands:");
        for (String string : supportedCommands) {
            this.writeln(string);
        }
    }

    /**
     * Writes to the system's console.
     *
     * @param object Object to write.
     */
    public void write(Object object) {
        System.out.print(object);
    }

    /**
     * Writes a line to the system's console.
     *
     * @param object Object to write.
     */
    public void writeln(Object object) {
        System.out.println(object);
    }

    /**
     * Starts an instance of the console.
     */
    public void start() {

        ConsoleExecutorChain chain = DatabaseConsole.getExecutionChain();
        ClientHandler handler = Client.getInstance().getHandler();

        String line;
        try {
            while ((line = consoleReader.readLine()) != null) {
                handler.setReceiving(true);
                chain.execute(line.trim());
                while (handler.isReceiving()) {
                    Thread.sleep(10);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (UserInterruptException e) {

        }
    }

    /**
     * Connects the elements of the given chain in their respected order.
     *
     * @param chainElements Elements of the chain to connect
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
        ConsoleExecutorChain server = new ServerExecutor();

        return DatabaseConsole.connectChain(new ConsoleExecutorChain[]{clear, exit, help, copy, server});
    }

}
