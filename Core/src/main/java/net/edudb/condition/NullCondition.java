/*
EduDB is made available under the OSI-approved MIT license.

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

package net.edudb.condition;

import net.edudb.data_type.DataType;

import java.util.ArrayList;

/**
 * A condition that is not useful when it comes to
 * filtering shards.
 * e.g. a condition that includes a different column than the
 * distribution column.
 *
 * @author Fady Sameh
 */
public class NullCondition extends Condition {

    public ArrayList<Condition> and(Condition condition) {
        ArrayList<Condition> result = new ArrayList<>();
        result.add(condition);
        return result;
    }


    /**
     * Since this condition is not decisive, any shard could contain records that satisfy
     * the condition
     */
    public boolean evaluate(DataType shardMinimum, DataType shardMaximum) { return true; }

    public String toString() {
        return "null condition";
    }

}
