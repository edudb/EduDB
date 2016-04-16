package net.edudb.relational_algebra;

public interface RAMatcherChain {
	
	public void setNextInChain(RAMatcherChain chainElement);
	
	public RAMatcherResult match(String string);
}
