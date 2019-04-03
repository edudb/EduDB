package net.edudb.distributed_plan;

import net.edudb.master.Master;
import net.edudb.master.MasterWriter;
import net.edudb.metadata_buffer.MetadataBuffer;
import net.edudb.operator.CreateTableOperator;
import net.edudb.query.QueryTree;
import net.edudb.response.Response;
import net.edudb.statement.SQLCreateTableStatement;
import net.edudb.statement.SQLStatement;
import net.edudb.structure.Record;

import java.util.Hashtable;

public class CreateTablePlan extends DistributedPlan {

    public QueryTree makePlan(SQLStatement sqlStatement) {
        SQLCreateTableStatement statement = (SQLCreateTableStatement) sqlStatement;
        QueryTree plan = null;
        String tableName = statement.getTableName();

        Hashtable<String, Record> tables = MetadataBuffer.getInstance().getTables();
        //MasterWriter.getInstance().write(new);
        //MasterWriter.getInstance().write(new Response("The table name is " + tableName));

        CreateTableOperator operator = new CreateTableOperator();
        operator.setParameter(statement);
        plan = new QueryTree(operator);

        return plan;
    }
}
