/*
EduDB is made available under the OSI-approved MIT license.

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

package net.edudb.join_request;

import net.edudb.data_type.DataType;

import java.util.Hashtable;

/**
 * A join request object stores information about a join operation
 *
 * @author Fady Sameh
 */
public abstract class JoinRequest {

    private Hashtable<String, DataType> leftShard;
    private Hashtable<String, DataType> rightShard;

    public JoinRequest() {

    }

    public JoinRequest(Hashtable<String, DataType> leftShard, Hashtable<String, DataType> rightShard) {
        this.leftShard = leftShard;
        this.rightShard = rightShard;
    }

    public Hashtable<String, DataType> getLeftShard() {
        return leftShard;
    }

    public void setLeftShard(Hashtable<String, DataType> leftShard) {
        this.leftShard = leftShard;
    }

    public Hashtable<String, DataType> getRightShard() {
        return rightShard;
    }

    public void setRightShard(Hashtable<String, DataType> rightShard) {
        this.rightShard = rightShard;
    }

}
