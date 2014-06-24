/*
EduDB is made available under the OSI-approved MIT license.

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/


package query_planner;

import gudusoft.gsqlparser.TCustomSqlStatement;
import operators.Operator;

/**
 * Created by mohamed on 4/1/14.
 */
public class PlanFactory implements Planer{
    /**
     * @uml.property  name="planner"
     * @uml.associationEnd  
     */
    private Planer planner;

    @Override
    public Operator makePlan(TCustomSqlStatement tCustomSqlStatement) {
        setPlanar(tCustomSqlStatement);
        if (planner == null){
            return null;
        }
        Operator plan = planner.makePlan(tCustomSqlStatement);
        return plan;
    }

   public void setPlanar(TCustomSqlStatement statement){
       System.out.println(statement.sqlstatementtype);
       switch (statement.sqlstatementtype){
           case sstcreatetable:
               planner = new CreateTablePlanner(); break;
           case sstselect:
               planner = new SelectPlanner();break;
           case sstinsert:
               planner= new InsertPlanner();break;
           case sstupdate:
               planner = new UpdatePlanner();break;
           default:
               System.out.println("Sorry! such statement not supported");
       }
   }
}
