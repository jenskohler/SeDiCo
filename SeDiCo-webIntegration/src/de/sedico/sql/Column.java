package de.sedico.sql;
/**
 * Diese Klasse erzeugt eine Spalte. Spalten verfügen über einen Namen und Wert.
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
