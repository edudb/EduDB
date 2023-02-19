/*
EduDB is made available under the OSI-approved MIT license.

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

package net.edudb.structure;

import java.io.Serializable;

/**
 * Created by mohamed on 4/19/14.
 * 
 * @author Ahmed Abdul Badie
 */
public class Column implements Serializable {

	private static final long serialVersionUID = -6271986181160247610L;

	/**
	 * @uml.property name="order"
	 */
	private int order;

	private String name;

	/**
	 * @uml.property name="tableName"
	 */
	private String tableName;

	private String typeName;

	public Column(int order) {
		this.order = order;
	}

	public Column(int num, String name, String tableName, String typeName) {
		this.order = num;
		this.tableName = tableName;
		this.name = name;
		this.typeName = typeName;
	}

	@Override
	public String toString() {
		// return order + ". " + tableName + "." + name + " : " + typeName;
		return name;
	}

	@Override
	public int hashCode() {
		return order;
	};

	@Override
	public boolean equals(Object o) {
		Column column = (Column) o;
		return column.order == order;// && column.name.equals(name) &&
										// column.tableName.equals(tableName);
	}

	public void setOrder(int order) {
		this.order = order;
	}

	public int getOrder() {
		return order;
	}

	public String getName() {
		return name;
	}

	public String getTableName() {
		return tableName;
	}

	public String getTypeName() {
		return typeName;
	}
}
