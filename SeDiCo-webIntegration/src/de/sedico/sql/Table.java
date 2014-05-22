package de.sedico.sql;

import java.util.*;
/**
 * Die Klasse implementiert eine Tabelle.
 * @author jens
 *
 */
public class Table {
    private final String tableName;
    private final List<Row> rows;
    private final List<ColumnDescriptor> columnDescriptors;
    private final List<ColumnDescriptor> primaryColumnDescriptors;

    public Table(String tableName, List<Row> rows, List<ColumnDescriptor> columnDescriptors) {
        if (columnDescriptors.size() == 0) {
            throw new IllegalArgumentException("Es wurden keine ColumnDescriptors übergeben.");
        }

        this.tableName = tableName;
        this.rows = rows;

        this.columnDescriptors = new ArrayList<ColumnDescriptor>();
        this.primaryColumnDescriptors = new ArrayList<ColumnDescriptor>();
        for (ColumnDescriptor descriptor : columnDescriptors) {
            if (descriptor.isPrimaryKey()) {
                this.primaryColumnDescriptors.add(descriptor);
            }
            else {
                this.columnDescriptors.add(descriptor);
            }
        }

        if (primaryColumnDescriptors.size() == 0) {
            throw new IllegalArgumentException("Unter den ColumnDescriptors befand sich kein Primary Key. Es sind nur Tabellen mit PK zulässig.");
        }
    }

    public String getTableName() {
        return tableName;
    }

    public List<Row> getRows() {
        return rows;
    }
/**
 * Diese Methode liefert die Spaltenbeschreiber zurück.
 * @return descriptors - Liste von Spaltenbeschreibern
 */
    public List<ColumnDescriptor> getColumnDescriptors() {
        List<ColumnDescriptor> descriptors = new ArrayList<ColumnDescriptor>();
        descriptors.addAll(primaryColumnDescriptors);
        descriptors.addAll(columnDescriptors);
        return descriptors;
    }

    public List<ColumnDescriptor> getValueColumnDescriptors() {
        return columnDescriptors;
    }

    public List<ColumnDescriptor> getPrimaryColumnDescriptors() {
        return primaryColumnDescriptors;
    }
/**
 * Diese Methode liefert einen Spaltenbeschreiber zurück.
 * @param columnName - Spaltenname
 * @return descriptor - falls übergebener String Spaltennamen entspricht
 */
    public ColumnDescriptor getColumnDescriptor(String columnName) {
        for(ColumnDescriptor descriptor : getColumnDescriptors()) {
            if (descriptor.getColumnName().equals(columnName)) {
                return descriptor;
            }
        }
        return null;
    }
}
