package net.edudb.table;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

public class Table implements Tabular, Serializable {

	private static final long serialVersionUID = -7002722265136341328L;

	/**
	 * Name of the table.
	 */
	private String name;

	/**
	 * Name of the pages in which data are persisted in.
	 */
	private ArrayList<String> pages;

	public Table(String name) {
		this.name = name;
		this.pages = new ArrayList<>();
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void addPageName(String pageName) {
		pages.add(pageName);
	}

	@Override
	public Collection<String> getPageNames() {
		return pages;
	}

}
