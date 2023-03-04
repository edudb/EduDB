package net.edudb.distributed_transaction;

import net.edudb.distributed_query.PostOrderTreeExecutor;
import net.edudb.distributed_query.QueryTree;

public class SynchronizedTransaction extends DistributedTransaction {

    public SynchronizedTransaction(QueryTree plan) {
        this.plan = plan;
        this.queryTreeExecutor = new PostOrderTreeExecutor();
    }

    public void run() {
        queryTreeExecutor.execute(plan);
    }
}
