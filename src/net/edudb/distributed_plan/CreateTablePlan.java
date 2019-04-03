package net.edudb.distributed_plan;

import net.edudb.master.MasterWriter;
import net.edudb.query.QueryTree;
import net.edudb.response.Response;
import net.edudb.statement.SQLCreateTableStatement;
import net.edudb.statement.SQLStatement;

public class CreateTablePlan extends DistributedPlan {

    public QueryTree makePlan(SQLStatement sqlStatement) {
        SQLCreateTableStatement statement = (SQLCreateTableStatement) sqlStatement;
        QueryTree plan = null;
        String tableName = statement.getTableName();

        MasterWriter.getInstance().write(new Response("The table name is " + tableName));

        return null;
    }
}
