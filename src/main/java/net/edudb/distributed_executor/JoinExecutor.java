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
import net.edudb.distributed_operator.JoinOperator;
import net.edudb.distributed_operator.parameter.JoinOperatorParameter;
import net.edudb.join_request.JoinRequest;
import net.edudb.join_request.LocalJoinRequest;
import net.edudb.master.MasterWriter;
import net.edudb.response.Response;
import net.edudb.statement.SQLSelectStatement;
import net.edudb.structure.Record;
import net.edudb.worker_manager.WorkerDAO;
import net.edudb.workers_manager.WorkersManager;

import java.util.ArrayList;
import java.util.Hashtable;

/**
 *
 *
 * @author Fady Sameh
 */
public class JoinExecutor implements OperatorExecutionChain {

    OperatorExecutionChain next;

    public void setNextElementInChain(OperatorExecutionChain chainElement) { this.next = chainElement; }

    public void execute(DistributedOperator operator) {
        if (operator instanceof JoinOperator join) {
            JoinOperatorParameter parameter = (JoinOperatorParameter)join.getParameter();

            SQLSelectStatement statement = parameter.getStatement();
            ArrayList<JoinRequest> joinRequests = parameter.getJoins();

            Response[] responses = new Response[joinRequests.size()];

            for (int i = 0; i < responses.length; i++) {

                JoinRequest joinRequest = joinRequests.get(i);
                Hashtable<String, DataType> leftShard = joinRequest.getLeftShard();
                Hashtable<String, DataType> rightShard = joinRequest.getRightShard();
                String joinStatement = statement.toString();

                if (joinRequest instanceof LocalJoinRequest) {
                    String workerAddress = leftShard.get("host").toString() + ":" + leftShard.get("port").toString();
                    WorkerDAO workerDAO = WorkersManager.getInstance().getWorkers().get(workerAddress);

                    if (workerDAO == null) {
                        MasterWriter.getInstance().write(new Response("Worker at '" + workerAddress + "' is not available"));
                        return;
                    }

                    int leftShardId = ((IntegerType) leftShard.get("id")).getInteger();

                    if (leftShardId != 0) {
                        String leftShardTable = leftShard.get("table_name").toString();
                        joinStatement = joinStatement.replace(leftShardTable, leftShardTable + leftShardId);
                    }

                    int rightShardId = ((IntegerType) rightShard.get("id")).getInteger();

                    if (rightShardId != 0) {
                        String rightShardTable = rightShard.get("table_name").toString();
                        joinStatement = joinStatement.replace(rightShardTable, rightShardTable + rightShardId);
                    }

                    final int index = i;
                    final String finalJoinStatement = joinStatement;

                    new Thread(() -> responses[index] = workerDAO.insert(finalJoinStatement)).start();
                }
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

            ArrayList<Record> concatenatedResult = new ArrayList<>();

            for (int i = 0; i < responses.length; i++) {
                concatenatedResult.addAll(responses[i].getRecords());
            }

            MasterWriter.getInstance().write(new Response("relation", concatenatedResult, null));

            //MasterWriter.getInstance().write(new Response("Reached Executor: " + parameter.getStatement().toString()));
        }
        else {
            next.execute(operator);
        }
    }
}
