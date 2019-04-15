/*
EduDB is made available under the OSI-approved MIT license.

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

package net.edudb.distributed_executor;


import net.edudb.distributed_operator.DistributedOperator;

public interface OperatorExecutionChain {

    /**
     * Connects an existing executor chain with the argument chain element.
     *
     * @param chainElement
     *            The {@link OperatorExecutionChain} to be set next in the
     *            chain.
     */
    public void setNextElementInChain(OperatorExecutionChain chainElement);

    /**
     * Executes the passed command. Classes that implement the
     * {@link OperatorExecutionChain} interface handles the execution of the
     * relational algerba operators according to their functionality.
     *
     * @param operator
     *            The operator to execute.
     */
    public void execute(DistributedOperator operator);

    /**
     * connects a chain of executors
     *
     * @param chain
     * The chain of executors
     *
     * @return
     * First executor in chain
     */
    public static OperatorExecutionChain connnectChain(OperatorExecutionChain[] chain) {
        for (int i = 0; i < chain.length - 1; i++) {
            chain[i].setNextElementInChain(chain[i+1]);
        }
        return chain[0];
    }

}
