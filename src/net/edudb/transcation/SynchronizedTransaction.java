package net.edudb.transcation;

import net.edudb.query.PostOrderTreeExecutor;
import net.edudb.query.QueryTree;
import net.edudb.query.QueryTreeExecutor;
import net.edudb.relation.Relation;
import net.edudb.relation.RelationIterator;
import net.edudb.server.ServerWriter;

public class SynchronizedTransaction implements Transaction {
	private QueryTree plan;
	private QueryTreeExecutor queryTreeExecutor;

	public SynchronizedTransaction(QueryTree plan) {
		this.plan = plan;
		this.queryTreeExecutor = new PostOrderTreeExecutor();
	}

	public void run() {
		Relation r = queryTreeExecutor.execute(plan);
		
		RelationIterator ri = r.getIterator();
		while (ri.hasNext()) {
			ServerWriter.getInstance().writeln(ri.next());
		}
	}

}
