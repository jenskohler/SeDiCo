package com.sedico.generictableadapter;
/**
 * Diese Klasse implementiert den Kolumnentyp
 * @author jens
 * @version 1.0
 */
public class ColumnType {
	/**
	 * Membervariablen
	 */
	private String columnType;
	private String columnName;
	
	/**
	 * Getter der Membervariable columnType
	 * @return columnType - ColumnType
	 */
	public String getColumnType() {
		return columnType;
	}
	/**
	 * Setter der Membervariable columnType
	 * @param columnType
	 */
	public void setColumnType(String columnType) {
		this.columnType = columnType;
	}
	/**
	 * Getter der Membervariable columnName
	 * @return columnName - Name der Column
	 */
	public String getColumnName() {
		return columnName;
	}
	/**
	 * Setter der Membervariable columnName
	 * @param columnName- Name der Column
	 */
	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}
	
	
	
}
