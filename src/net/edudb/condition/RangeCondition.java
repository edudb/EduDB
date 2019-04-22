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

    public ArrayList<Condition> and(Condition condition) {
        Condition resultCondition = null;
        if (condition instanceof NullCondition) {
            resultCondition = this;
        } else if (condition instanceof EmptyCondition) {
            resultCondition = condition;
        } else if (condition instanceof EqualsCondition) {
            resultCondition = merge((EqualsCondition) condition, this);
        }

        if (resultCondition != null) {
            ArrayList<Condition> results = new ArrayList<>();
            results.add(resultCondition);
            return results;
        }

        ArrayList<Condition> results = new ArrayList<>();
        if (condition instanceof NotEqualsCondition) {
            ArrayList<Condition> mergingResults = merge((NotEqualsCondition)condition, this);
            for (Condition c: mergingResults)
                results.add(c);
        }
        else if (condition instanceof RangeCondition) {
            DataType minimum = null;
            boolean minimumInclusive = false;
            DataType maximum = null;
            boolean maximumInclusive = false;

            if (this.getMinimumBound() == null && ((RangeCondition) condition).getMinimumBound() != null) {
                minimum = ((RangeCondition) condition).getMinimumBound();
                minimumInclusive = ((RangeCondition) condition).isMinimumInclusive();
            }
            else if (this.getMinimumBound() != null && ((RangeCondition) condition).getMinimumBound() == null) {
                minimum = this.getMinimumBound();
                minimumInclusive = this.isMinimumInclusive();
            }
            else if (this.getMinimumBound() != null && ((RangeCondition) condition).getMinimumBound() != null) {
                if (this.getMinimumBound().compareTo(((RangeCondition) condition).getMinimumBound()) > 0) {
                    minimum = this.getMinimumBound();
                    minimumInclusive = this.isMinimumInclusive();
                }
                else {
                    minimum = ((RangeCondition) condition).getMinimumBound();
                    minimumInclusive = ((RangeCondition) condition).isMinimumInclusive();
                }
            }

            if (this.getMaximumBound() == null && ((RangeCondition) condition).getMaximumBound() != null) {
                maximum = ((RangeCondition) condition).getMaximumBound();
                maximumInclusive = ((RangeCondition) condition).isMaximumInclusive();
            }
            else if (this.getMaximumBound() != null && ((RangeCondition) condition).getMaximumBound() == null) {
                maximum = this.getMaximumBound();
                maximumInclusive = this.isMaximumInclusive();
            }
            else if (this.getMaximumBound() != null && ((RangeCondition) condition).getMaximumBound() != null) {
                if (this.getMaximumBound().compareTo(((RangeCondition) condition).getMaximumBound()) < 0) {
                    maximum = this.getMaximumBound();
                    maximumInclusive = this.isMaximumInclusive();
                }
                else {
                    maximum = ((RangeCondition) condition).getMaximumBound();
                    maximumInclusive = ((RangeCondition) condition).isMaximumInclusive();
                }
            }

            Condition condition1 = null;
            if (minimum != null && maximum != null) {
                if (minimum.compareTo(maximum) > 0)
                    condition1 = new EmptyCondition();
                if (minimum.compareTo(maximum) == 0) {
                    if (!minimumInclusive || !maximumInclusive)
                        condition1 = new EmptyCondition();
                    else
                        condition1 = new EqualsCondition(minimum);
                }

            }

            if (condition1 == null)
                condition1 = new RangeCondition(minimum, minimumInclusive, maximum, maximumInclusive);

            results.add(condition1);

        }

        return results;
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
            if (maximumInclusive && minimumInclusive) {
                if (minimumBound.compareTo(shardMaximum) <= 0
                        && maximumBound.compareTo(shardMinimum) >= 0)
                    return true;
            }
            else if (!maximumInclusive && !minimumInclusive) {
                if (minimumBound.compareTo(shardMaximum) < 0
                        && maximumBound.compareTo(shardMinimum) > 0)
                    return true;
            }
            else if (!maximumInclusive && minimumInclusive) {
                if (minimumBound.compareTo(shardMaximum) <= 0
                        && maximumBound.compareTo(shardMinimum) > 0)
                    return true;
            }
            else {
                if (minimumBound.compareTo(shardMaximum) < 0
                        && maximumBound.compareTo(shardMinimum) >= 0)
                    return true;
            }
        }
        return false;
    }

    public DataType getMinimumBound() {
        return minimumBound;
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
