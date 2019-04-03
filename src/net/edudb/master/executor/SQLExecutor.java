/*
EduDB is made available under the OSI-approved MIT license.

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

package net.edudb.master.executor;

import net.edudb.distributed_parser.DistributedParser;
import net.edudb.distributed_plan.DistributedPlanFactory;
import net.edudb.master.MasterWriter;
import net.edudb.query.QueryTree;
import net.edudb.response.Response;
import net.edudb.statement.SQLStatement;

/**
 * This executor is responsible for executing SQL statements
 *
 * @author Fady Sameh
 */
public class SQLExecutor implements MasterExecutorChain {

    private DistributedPlanFactory planFactory = new DistributedPlanFactory();

    @Override
    public void setNextElementInChain(MasterExecutorChain chainElement) {
    }

    @Override
    public void execute(String string) {

        DistributedParser parser = new DistributedParser();
        SQLStatement statement = parser.parseSQL(string.replace(":", ""));
        if (statement != null) {
            //MasterWriter.getInstance().write(new Response(statement.toString()));
            QueryTree plan = planFactory.makePlan(statement);
            if (plan != null) {
                MasterWriter.getInstance().write(new Response("plan generated"));
            }
        }

    }
}
