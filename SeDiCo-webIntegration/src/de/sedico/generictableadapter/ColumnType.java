package de.sedico.generictableadapter;
/**
 * Diese Klasse erzeugt den Spaltentypen.
 * @author jens
 *
 */
public class ColumnType {
	private String 		columnType;
	private String 		columnName;

	
	public ColumnType(String columnName, String columnType) {
		this.columnName = columnName;
		this.columnType = columnType;

	}
	
	public String getColumnType() {
		return columnType;
	}
	public void setColumnType(String columnType) {
		this.columnType = columnType;
	}
	public String getColumnName() {
		return columnName;
	}
	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}

	
	
	
	
}
