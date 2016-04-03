package net.edudb.table;

import java.util.Collection;

public interface Tabular {

	/**
	 * 
	 * @return Name of the table.
	 */
	public String getName();

	/**
	 * 
	 * @return ArrayList<String> of page names holding the data.
	 */
	public Collection<String> getPageNames();

	/**
	 * Adds a new page name to the page names list.
	 * 
	 * @param pageName
	 *            Name of the page to add.
	 */
	public void addPageName(String pageName);

}
