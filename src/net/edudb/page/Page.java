package net.edudb.page;

import net.edudb.structure.Recordable;

public interface Page {
	
	/**
	 * 
	 * @return Name of the page.
	 */
	public String getName();
	
	/**
	 * 
	 * @return Records inside the page.
	 */
	public Recordable[] getRecords();
	
	/**
	 * Adds a record to the page.
	 * 
	 * @param record Record to be added to the page.
	 */
	public void addRecord(Recordable record);
	
	/**
	 * 
	 * @return Number of records the page can hold.
	 */
	public int capacity();
	
	/**
	 * 
	 * @return Number of records in the page.
	 */
	public int size();
	
	public boolean isFull();
	
	public boolean isEmpty();
	
	/**
	 * Increments the open count.
	 */
	public void open();
	
	/**
	 * Decrements the open count;
	 */
	public void close();
	
	public boolean isOpen();
	
	/**
	 * Prints the records to the console.
	 */
	public void print();

}
