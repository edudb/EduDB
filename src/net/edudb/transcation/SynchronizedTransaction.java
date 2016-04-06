package net.edudb.transcation;

import net.edudb.operator.Operator;

public class SynchronizedTransaction implements Transaction {
	private Operator plan;

	public SynchronizedTransaction(Operator plan) {
		this.plan = plan;
	}
	
	public void run() {
		plan.execute();
	}

}
