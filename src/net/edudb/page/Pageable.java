package net.edudb.page;

import net.edudb.structure.Recordable;

public interface Pageable {
	
	public String getName();
	
	public Recordable[] getRecords();
	
	public void addRecord(Recordable record);
	
	public int capacity();
	
	public int size();
	
	public boolean isFull();
	
	public boolean isEmpty();

}
