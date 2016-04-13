package net.edudb.transcation;

import net.edudb.db_operator.DBOperator;

public class SynchronizedTransaction implements Transaction {
	private DBOperator plan;

	public SynchronizedTransaction(DBOperator plan) {
		this.plan = plan;
	}
	
	public void run() {
		plan.execute();
	}

}
