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

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Prints EduDB's supported commands.
 *
 * @author Ahmed Abdul Badie
 */
public class HelpExecutor implements ConsoleExecutorChain {
    private ConsoleExecutorChain nextChainElement;
    private final Map<String, String> commands;

    public HelpExecutor() {
        commands = new LinkedHashMap<>();
        addCommand("clear", "Clears the console");
        addCommand("exit", "Close the client");
        addCommand("COPY table_name FROM 'path' DELIMITER 'delimiter'", "Copies data from a file to a table");
        addCommand("OPEN DATABASE database_name", "Opens a database");
        addCommand("CLOSE DATABASE", "Closes the current database");
        addCommand("CREATE DATABASE database_name", "Creates a database");
        addCommand("DROP DATABASE database_name", "Drops a database");
        addCommand("CREATE USER user_name WITH PASSWORD=\"password\" [AS ADMIN|WORKSPACE_ADMIN|USER] [IN WORKSPACE=\"workspace_name\"]", "Creates a user");
        addCommand("DROP USER user_name FROM WORKSPACE=\"workspace_name\"", "Drops a user");
        addCommand("CREATE INDEX ON table_name(column_name)", "Creates a B+ tree index on a column");
        addCommand("DROP INDEX ON table_name(column_name)", "Drops the index on a column");
        addCommand("CREATE TABLE table_name (column_type_list)", "Creates a table");
        addCommand("DROP TABLE table_name", "Drops a table");
        addCommand("DROP WORKSPACE workspace_name", "Drops a workspace");
        addCommand("INSERT INTO table_name VALUES (values)", "Inserts a row into a table");
        addCommand("DELETE FROM table_name [WHERE condition]", "Deletes rows from a table");
        addCommand("UPDATE table_name SET column_value_list [WHERE condition]", "Updates rows in a table");
        addCommand("SELECT column_name_list FROM table_name [WHERE condition]", "Selects rows from a table");
    }

    private void addCommand(String command, String description) {
        commands.put(command, description);
    }

    @Override
    public void setNextElementInChain(ConsoleExecutorChain chainElement) {
        this.nextChainElement = chainElement;
    }

    @Override
    public Response execute(String input) {
        if (input.equalsIgnoreCase("help")) {
            StringBuilder helpText = new StringBuilder("Supported commands:\n");
            for (Map.Entry<String, String> entry : commands.entrySet()) {
                helpText.append("\t").append(entry.getKey()).append("    -    ").append(entry.getValue()).append("\n");
            }
            return new Response(helpText.toString());
        }
        return nextChainElement.execute(input);
    }

}
