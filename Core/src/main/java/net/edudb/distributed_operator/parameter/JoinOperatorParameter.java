package net.edudb.distributed_operator.parameter;

import net.edudb.join_request.JoinRequest;
import net.edudb.statement.SQLSelectStatement;

import java.util.ArrayList;

public class JoinOperatorParameter implements DistributedOperatorParameter {

    SQLSelectStatement statement;
    ArrayList<JoinRequest> joins;

    public JoinOperatorParameter(SQLSelectStatement statement, ArrayList<JoinRequest> joins) {
        this.statement = statement;
        this.joins = joins;
    }

    public SQLSelectStatement getStatement() {
        return statement;
    }

    public void setStatement(SQLSelectStatement statement) {
        this.statement = statement;
    }

    public ArrayList<JoinRequest> getJoins() {
        return joins;
    }

    public void setJoins(ArrayList<JoinRequest> joins) {
        this.joins = joins;
    }
}
