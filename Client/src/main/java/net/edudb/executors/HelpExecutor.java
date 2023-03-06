/*
 *
 * EduDB is made available under the OSI-approved MIT license.
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 * /
 */

package net.edudb.executors;


import net.edudb.Response;

/**
 * Prints EduDB's supported commands.
 *
 * @author Ahmed Abdul Badie
 */
public class HelpExecutor implements ConsoleExecutorChain {
    private ConsoleExecutorChain nextChainElement;

    private String[] commands = { // TODO: refactor this
            "\tclear", "\tCLOSE DATABASE",
            "\tCOPY table_name FROM 'path' DELIMITER 'delimiter'", "\texit", "\tOPEN DATABASE database_name", "\tLIST DATABASES",
            "\tSQL commands:", "\t\tCREATE DATABASE database_name", "\t\tCREATE TABLE table_name (column_type_list)",
            "\t\tDELETE FROM table_name [WHERE condition]", "\t\tDROP DATABASE database_name",
            "\t\tINSERT INTO table_name VALUES (values)", "\t\tUPDATE table_name SET column_value_list [WHERE condition]"
    };

    @Override
    public void setNextElementInChain(ConsoleExecutorChain chainElement) {
        this.nextChainElement = chainElement;
    }

    @Override
    public Response execute(String string) {
        if (string.equalsIgnoreCase("help")) {
            StringBuilder helpText = new StringBuilder("Supported commands:");
            for (String command : commands) {
                helpText.append(command).append("\n");
            }
            return new Response(helpText.toString());
        }
        return nextChainElement.execute(string);
    }

}
