/*
EduDB is made available under the OSI-approved MIT license.

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

package net.edudb.distributed_executor;

import net.edudb.data_type.DataType;
import net.edudb.data_type.IntegerType;
import net.edudb.distributed_operator.DistributedOperator;
import net.edudb.distributed_operator.InsertOperator;
import net.edudb.distributed_operator.parameter.InsertOperatorParamater;
import net.edudb.master.MasterWriter;
import net.edudb.response.Response;
import net.edudb.statement.SQLInsertStatement;
import net.edudb.worker_manager.WorkerDAO;
import net.edudb.workers_manager.WorkersManager;

import java.util.ArrayList;
import java.util.Hashtable;

/**
 * Inserts a record in all the necessary worker databases
 *
 * @author Fady Sameh
 */
public class InsertExecutor implements OperatorExecutionChain {

    OperatorExecutionChain next;

    public void setNextElementInChain(OperatorExecutionChain chainElement) { this.next = chainElement; }

    public void execute(DistributedOperator operator) {
        if (operator instanceof InsertOperator) {
            DistributedOperator insert = (InsertOperator) operator;
            InsertOperatorParamater parameter = (InsertOperatorParamater) insert.getParameter();

            SQLInsertStatement statement = parameter.getStatement();
            ArrayList<Hashtable<String, DataType>> shards = parameter.getShards();

            Response[] responses = new Response[shards.size()];

            for (int i = 0; i < responses.length; i++) {
                Hashtable<String, DataType> shard = shards.get(i);
                String workerAddress = shard.get("host").toString() + ":" + shard.get("port").toString();
                WorkerDAO workerDAO = WorkersManager.getInstance().getWorkers().get(workerAddress);

                if (workerDAO == null) {
                    MasterWriter.getInstance().write(new Response("Worker at '" + workerAddress + "' is not available"));
                    return;
                }

                String tableName = statement.getTableName();
                int id = ((IntegerType)shard.get("id")).getInteger();
                String insertStatement = statement.toString();

                if (id != 0) {
                    insertStatement = insertStatement.replace(tableName, tableName + id);
                }

                final int index = i;
                final String finalInsertStatement = insertStatement;

                new Thread(() -> responses[index] = workerDAO.insert(finalInsertStatement)).start();
            }

            int index = 0;
            int responsesReceived = 0;

            while (responsesReceived != responses.length) {
                if (responses[index] == null)
                    responsesReceived = 0;
                else
                    ++responsesReceived;

                index = (index + 1) % responses.length;
            }

            MasterWriter.getInstance().write(new Response("Insertion complete", null, ""));

        }
        else {
            next.execute(operator);
        }
    }
}
