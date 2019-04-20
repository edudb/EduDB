/*
EduDB is made available under the OSI-approved MIT license.

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

package net.edudb.condition;

import net.edudb.data_type.DataType;

/**
 * A range condition. e.g. 18 < age < 21
 * It is used for all range operators: <, <=, >, >=
 *
 * @author Fady Sameh
 */
public class RangeCondition extends Condition {

    private DataType minimumBound;
    private boolean minimumInclusive;

    private DataType maximumBound;
    private boolean maximumInclusive;

    public RangeCondition(DataType minimumBound, boolean minimumInclusive, DataType maximumBound, boolean maximumInclusive) {
        this.minimumBound = minimumBound;
        this.minimumInclusive = minimumInclusive;
        this.maximumBound = maximumBound;
        this.maximumInclusive = maximumInclusive;
    }

    Condition and(Condition condition) {
        return null;
    }

    Condition or(Condition condition) {
        return null;
    }

    public DataType getMinimumBound() {
        return minimumBound;
    }

    /**
     * This condition is satisfied if the value range of the shard and the
     * condition overlap
     */
    public boolean evaluate(DataType shardMinimum, DataType shardMaximum) {
        if (minimumBound == null) {
            if (maximumInclusive) {
                return shardMinimum.compareTo(maximumBound) <= 0;
            }
            else {
                return shardMinimum.compareTo(maximumBound) < 0;
            }
        }
        else if (maximumBound == null) {
            if (minimumInclusive) {
                return shardMaximum.compareTo(minimumBound) >= 0;
            }
            else {
                return shardMaximum.compareTo(minimumBound) > 0;
            }
        }
        else {
            return false;
        }
    }

    public boolean isMinimumInclusive() {
        return minimumInclusive;
    }

    public DataType getMaximumBound() {
        return maximumBound;
    }

    public boolean isMaximumInclusive() {
        return maximumInclusive;
    }

    public String toString() {
        String leftCondition = "";
        if (minimumBound != null) {
            leftCondition += minimumBound.toString();
            leftCondition += (isMinimumInclusive()) ? " <= " : " < ";
        }

        String rightCondition = "";
        if (maximumBound != null) {
            rightCondition += (isMaximumInclusive()) ? " <= " : " < ";
            rightCondition += maximumBound.toString();
        }

        return leftCondition + "value" + rightCondition;
    }

}
