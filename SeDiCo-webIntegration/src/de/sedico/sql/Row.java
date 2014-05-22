package de.sedico.sql;

import java.util.*;
/**
 * Diese Klasse implementiert eine Zeile.
 * @author jens
 *
 */
public class Row {
    private final Map<String, Column> columns = new HashMap<String, Column>();

    public Row(List<Column> columns) {
        for (Column column : columns) {
            this.columns.put(column.getColumnName(), column);
        }
    }

    public Collection<Column> getColumns() {
        return columns.values();
    }
/**
 * Diese Methode liefert die Zeilen anhand des Spaltennamens als Schlüssel zurück
 * @param columnName - Spaltenname
 * @return columns.get(columnName)- liefert die Spaltennamen
 */
    public Column getColumn(String columnName) {
        if(columns.containsKey(columnName)) {
            return columns.get(columnName);
        }

        return null;
    }
}
