package com.sedico.sql;
/**
 * Diese Klasse implementiert eine Spalte. Eine Spalte besitzt einen Namen und einen Wert.
 * @author jens
 *
 */
public class Column {
    private final String columnName;
    private final Object columnValue;

    public Column(String columnName, Object columnValue) {
        this.columnName = columnName;
        this.columnValue = columnValue;
    }

    public Object getColumnValue() {
        return columnValue;
    }

    public String getColumnName() {
        return columnName;
    }
}
