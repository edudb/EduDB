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
 * An equality condition. e.g. age = 21
 *
 * @author Fady Sameh
 */
public class EqualsCondition extends Condition {

    private DataType data;

    public EqualsCondition(DataType data) {
        this.data = data;
    }

    public ArrayList<Condition> and(Condition condition) {
        Condition resultCondition = null;
        if (condition instanceof NullCondition) {
            resultCondition =  this;
        }
        else if (condition instanceof EmptyCondition) {
            resultCondition =  condition;
        }
        else if (condition instanceof EqualsCondition) {
            if (this.getData().compareTo(((EqualsCondition) condition).getData()) == 0)
                resultCondition = this;
            else
                resultCondition =  new EmptyCondition();
        }
        else if (condition instanceof NotEqualsCondition) {
            resultCondition = merge(this, (NotEqualsCondition)condition);
        }
        else if (condition instanceof RangeCondition) {
            resultCondition = merge(this, (RangeCondition)condition);
        }

        if (resultCondition == null)
            return null;

        ArrayList<Condition> result = new ArrayList<>();
        result.add(resultCondition);
        return result;
    }

    /**
     * This condition is satisfied is satisfied if and only if the condition value
     * lies between the shard's minimum and maximum values
     */
    public boolean evaluate(DataType shardMinimum, DataType shardMaximum) {
        return (data.compareTo(shardMinimum) >= 0) && (data.compareTo(shardMaximum) <= 0);
    }

    public DataType getData() {
        return data;
    }

    @Override
    public String toString() {
        return "= " + data.toString();
    }
}
