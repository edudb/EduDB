package net.edudb.page;

import java.io.Serializable;

import net.edudb.engine.Config;
import net.edudb.engine.Utility;
import net.edudb.server.ServerWriter;
import net.edudb.structure.DBRecord;
import net.edudb.structure.Recordable;

public class Page implements Pageable, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4813060042690551966L;
	
	private String name;
	private Recordable[] records;
	private int nextLocation;
	
	public Page() {
		this.name = Utility.generateUUID();
		this.records = new DBRecord[Config.pageSize()];
		this.nextLocation = 0;
	}
	
	public void print() {
		for (int i = 0; i < nextLocation; ++i) {
			ServerWriter.getInstance().writeln(records[i]);
		}
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public Recordable[] getRecords() {
		return records;
	}

	@Override
	public void addRecord(Recordable record) {
		if (nextLocation <= Config.pageSize()) {
			records[nextLocation++] = record;
		}
	}

	@Override
	public int capacity() {
		return records.length;
	}

	@Override
	public int size() {
		return nextLocation;
	}

	@Override
	public boolean isFull() {
		return size() > Config.pageSize();
	}

	@Override
	public boolean isEmpty() {
		return size() == 0;
	}
	
}
