/*
EduDB is made available under the OSI-approved MIT license.

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

package net.edudb.condition;

import net.edudb.data_type.DataType;

import java.util.ArrayList;

public abstract class Condition {
    String type;

    abstract public ArrayList<Condition> and(Condition condition);

    /**
     * checks whether values inside a shard could satisfy the condition
     *
     * @param shardMinimum
     * @param shardMaximum
     * @return
     */
    abstract public boolean evaluate(DataType shardMinimum, DataType shardMaximum);

    protected static Condition merge(EqualsCondition equalsCondition, NotEqualsCondition notEqualsCondition) {
        if (equalsCondition.getData().compareTo(notEqualsCondition.getData()) == 0)
            return new EmptyCondition();
        else {
            return equalsCondition;
        }
    }

    protected static Condition merge(EqualsCondition equalsCondition, RangeCondition rangeCondition) {

        if (!(rangeCondition.getMinimumBound() == null)) {
            if (rangeCondition.isMinimumInclusive()) {
                if (equalsCondition.getData().compareTo(rangeCondition.getMinimumBound()) < 0)
                    return new EmptyCondition();
            }
            else {
                if (equalsCondition.getData().compareTo(rangeCondition.getMinimumBound()) <= 0)
                    return new EmptyCondition();
            }
        }


        if (!(rangeCondition.getMaximumBound() == null)) {
            if (rangeCondition.isMaximumInclusive()) {
                if (equalsCondition.getData().compareTo(rangeCondition.getMaximumBound()) > 0)
                    return new EmptyCondition();
            }
            else {
                if (equalsCondition.getData().compareTo(rangeCondition.getMaximumBound()) >= 0)
                    return new EmptyCondition();
            }
        }

        return equalsCondition;
    }

    protected static ArrayList<Condition> merge (NotEqualsCondition notEqualsCondition, RangeCondition rangeCondition) {
        ArrayList<Condition> results = new ArrayList<>();

        if (rangeCondition.getMaximumBound() == null) {
            if (rangeCondition.getMinimumBound().compareTo(notEqualsCondition.getData()) < 0)
                results.add(new RangeCondition(rangeCondition.getMinimumBound(), rangeCondition.isMinimumInclusive(),
                        notEqualsCondition.getData(), false));
                results.add(new RangeCondition(notEqualsCondition.getData(), false, null,
                        false));
        }
        else if (rangeCondition.getMinimumBound() == null) {
            if (rangeCondition.getMaximumBound().compareTo(notEqualsCondition.getData()) > 0)
                results.add(new RangeCondition(notEqualsCondition.getData(), false,
                        rangeCondition.getMaximumBound(), rangeCondition.isMaximumInclusive()));
                results.add(new RangeCondition(null, false, notEqualsCondition.getData(),
                        false));
        }
        else {
            if (rangeCondition.getMinimumBound().compareTo(notEqualsCondition.getData()) < 0
                    && rangeCondition.getMaximumBound().compareTo(notEqualsCondition.getData()) > 0) {
                results.add(new RangeCondition(rangeCondition.getMinimumBound(), rangeCondition.isMinimumInclusive(),
                        notEqualsCondition.getData(), false));
                results.add(new RangeCondition(notEqualsCondition.getData(), false,
                        rangeCondition.getMaximumBound(), rangeCondition.isMaximumInclusive()));
            }

        }

        if (results.isEmpty())
            results.add(new EmptyCondition());

        return results;
    }

}
