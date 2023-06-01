/*
 *
 * EduDB is made available under the OSI-approved MIT license.
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 * /
 */

package net.edudb.executors;


import net.edudb.Client;
import net.edudb.Response;
import net.edudb.ResponseStatus;
import net.edudb.jdbc.EdudbResultSet;
import net.edudb.structure.Record;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;


public class ForwardQueryToServerExecutor implements ConsoleExecutorChain {
    private ConsoleExecutorChain nextElement;

    @Override
    public void setNextElementInChain(ConsoleExecutorChain chainElement) {
        this.nextElement = chainElement;
    }

    @Override
    public Response execute(String command) {
        if (!command.toLowerCase().startsWith("select")) {
            return nextElement.execute(command);
        }

        try (Statement statement = Client.getInstance().getConnection().createStatement()) {
            EdudbResultSet resultSet = (EdudbResultSet) statement.executeQuery(command);
            ArrayList<Record> records = (ArrayList<Record>) resultSet.fetchAllAndGetRecords();
            Response response = new Response("Executed successfully", records);
            response.setStatus(ResponseStatus.OK);
            return response;
        } catch (SQLException e) {
            return new Response(e.getMessage(), ResponseStatus.ERROR);
        }
    }

}
