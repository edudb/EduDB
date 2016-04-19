package net.edudb.transcation;

import net.edudb.query.QueryTree;
import net.edudb.query.QueryTreeExecutor;

public abstract class Transaction {
	protected QueryTree plan;
	protected QueryTreeExecutor queryTreeExecutor;
}
