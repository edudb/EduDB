/*
 *
 * EduDB is made available under the OSI-approved MIT license.
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 * /
 */

package net.edudb.operator.executor;

/**
 * Executes a query tree in a post-order strategy. If a tree node is not a leaf,
 * its left child is executed before its right child. This approach is achieved
 * recursively.
 *
 * @author Ahmed Abdul Badie
 */
public class PostOrderOperatorExecutor {

    public OperatorExecutionChain getChain() {

        OperatorExecutionChain[] ExecutorsList = {
                new ProjectExecutor(),
                new CartesianProductExecutor(),
                new EquiJoinExecutor(),
                new FilterExecutor(),
                new IndexFilterExecutor(),
                new RelationExecutor(),
                new InsertExecutor(),
                new UpdateTableExecutor(),
                new DeleteExecutor(),
                new CreateTableExecutor(),
        };

        for (int i = 0; i < ExecutorsList.length - 1; i++) {
            ExecutorsList[i].setNextElementInChain(ExecutorsList[i + 1]);
        }

        return ExecutorsList[0];
    }

}
