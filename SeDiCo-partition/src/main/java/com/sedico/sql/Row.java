package com.sedico.sql;

import java.util.*;
/**
 * Diese Klasse implementiert eine Reihe einer Tabelle. 
 * @author jens
 *
 */
public class Row {
    private final Map<String, Column> columns = new HashMap();
    /**
     * Der Konstruktor fügt den Spaltennamen als Schlüssel und die Spalte als Wert einer Map hinzu.
     * @param columns - Liste von Spalten
     */
    public Row(List<Column> columns) {
        for (Column column : columns) {
            this.columns.put(column.getColumnName(), column);
        }
    }
    /**
     * Diese Methode gibt alle Spalten als Collection zurück
     * @return columns.values() - Die Werte der Map werden zurückgegeben
     */
    public Collection<Column> getColumns() {
        return columns.values();
    }
    /**
     * Diese Methode gibt die Spalte anhand eines übergebenen Strings zurück, falls dieser als Schlüssel enthalten ist.
     * @param columnName - Name der Spalte
     * @return columns.get(columnName) - Die Spalte mit dem übereinstimmenden String wird zurückgegeben
     */
    public Column getColumn(String columnName) {
        if(columns.containsKey(columnName)) {
            return columns.get(columnName);
        }

        return null;
    }
}
