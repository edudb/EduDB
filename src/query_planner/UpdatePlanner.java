/*
EduDB is made available under the OSI-approved MIT license.

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/


package query_planner;

import DBStructure.DBColumn;
import dataTypes.DB_Type;
import gudusoft.gsqlparser.EExpressionType;
import gudusoft.gsqlparser.TCustomSqlStatement;
import gudusoft.gsqlparser.nodes.TExpression;
import gudusoft.gsqlparser.nodes.TParseTreeNode;
import gudusoft.gsqlparser.stmt.TUpdateSqlStatement;
import operators.*;

import java.util.ArrayList;


public class UpdatePlanner implements Planer{
    @Override
    public Operator makePlan(TCustomSqlStatement tCustomSqlStatement) {
        TUpdateSqlStatement statement = (TUpdateSqlStatement) tCustomSqlStatement;
        ArrayList<DBAssignment> assignments = new ArrayList<>();

        String tableName = statement.getTargetTable().toString();

        // extract assignments
        for(int i=0;i<statement.getResultColumnList().size();i++){
            TParseTreeNode assignment = statement.getResultColumnList().elementAt(i);
            DBAssignment assignment1 = new DBAssignment(assignment.getStartToken().toString(),
                    assignment.getEndToken().toString(),tableName );
            assignments.add(assignment1);
        }

        // extract conditions
        TExpression expression = statement.getWhereClause().getCondition();
        if (expression.getExpressionType() == EExpressionType.simple_comparison_t){
            String leftString = expression.getLeftOperand().toString();
            DBColumn column1 = new DBColumn(leftString, tableName);
            TExpression right = expression.getRightOperand();
            String rightString = right.toString();
            if (right.getExpressionType()  == EExpressionType.simple_constant_t){
                DB_Type.DB_Int constant = new DB_Type.DB_Int(rightString);
                DBCondition condition = new DBCondition(column1,constant,
                        expression.getOperatorToken().toString().charAt(0));
                UpdateOp update = new UpdateOp(tableName, assignments, condition);
                return update;
            }else{
                DBColumn column2 = new DBColumn(rightString, tableName);
                DBCondition condition = new DBCondition(column1,column2,
                        expression.getOperatorToken().toString().charAt(0));
                UpdateOp update = new UpdateOp(tableName, assignments, condition);
                return update;
            }
        }
        return null;
    }
}
