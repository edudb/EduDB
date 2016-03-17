/*
EduDB is made available under the OSI-approved MIT license.

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/


package net.edudb.operator;

import gudusoft.gsqlparser.nodes.TExpression;
import net.edudb.data_type.DB_Type;
import net.edudb.data_type.DataType;
import net.edudb.structure.DBColumn;
import net.edudb.structure.DBConst;
import net.edudb.structure.DBRecord;

/**
 * Created by mohamed on 4/19/14.
 */
public class DBCondition implements DBCond {
    /**
     * @uml.property  name="column1"
     * @uml.associationEnd  multiplicity="(1 1)"
     */
    DBColumn column1;
    /**
     * @uml.property  name="column2"
     * @uml.associationEnd  multiplicity="(1 1)"
     */
    DBParameter column2;
    /**
     * @uml.property  name="op"
     */
    char op;

    public DBCondition(DBColumn column, DBParameter column2, char op) {
        column1 = column;
        this.column2 = column2;
        this.op = op;
    }

    @Override
    public int numOfParameters() {
        return 0;
    }

    public void print() {
        System.out.print(column1.toString() + " " + op + " "
                + column2.toString());
    }

    @Override
    public String toString() {
        return column1.toString() + " " + op + " " + column2.toString();
    }

    @Override
    public boolean evaluate(DBRecord dbRecord) {
        DataType value1 = dbRecord.getValue(column1);
        if (column2 instanceof DBColumn){
            DataType value2 = dbRecord.getValue( ( (DBColumn) column2 ).order-1);
            return MathUtil.evaluateCond(value1, value2, op);
        }
        if (column2 instanceof DBConst){
            return MathUtil.evaluateCond(value1,(DB_Type.DB_Int) column2, op);
        }
        return false;
    }

    public static DBCond getCondition(TExpression expression){
        //todo differnt condition types
        /*switch (expression.getExpressionType()){
            case simple_comparison_t:
            {
                TExpression exp1 = expression.getLeftOperand();
                if (exp1.getExpressionType() == )
                DBCondition condition = new DBCondition();

            }
        }*/
        System.out.println(expression.getExpressionType());
        System.out.println(expression.getLeftOperand().getExpressionType());
        System.out.println(expression.getRightOperand().getExpressionType());
        return null;
    }
}
