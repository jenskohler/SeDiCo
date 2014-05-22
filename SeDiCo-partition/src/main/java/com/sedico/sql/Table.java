package com.sedico.sql;

import java.util.*;

import org.hibernate.dialect.Dialect;
/**
 * Diese Klasse implementiert eine Tabelle. Eine Tabelle hat eine Name, eine Liste von Reihen und Spaltenbeschreibern. 
 * @author jens
 *
 */
public class Table {
    private final String tableName;
    private final List<Row> rows;
    private final List<ColumnDescriptor> columnDescriptors;
    private final List<ColumnDescriptor> primaryColumnDescriptors;
/**
 * Der Konstruktor fügt primären Spaltenbeschreiber zur Liste der primären Spaltenbeschreiber hinzu.
 * Die anderen Spaltenbeschreiber werden zur Liste der Spaltenbeschreiber hinzugefügt.
 * @param tableName - Name der Tabelle
 * @param rows - Liste von Zeilen
 * @param columnDescriptors - Liste von Spaltenbeschreibern
 */
    public Table(String tableName, List<Row> rows, List<ColumnDescriptor> columnDescriptors) {
        if (columnDescriptors.size() == 0) {
            throw new IllegalArgumentException("Es wurden keine ColumnDescriptors übergeben.");
        }

        this.tableName = tableName;
        this.rows = rows;
       
        this.columnDescriptors = new ArrayList();
        this.primaryColumnDescriptors = new ArrayList();
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
 * Diese Methode liefert die primären und normalen Spaltenbeschreiber zurück.
 * @return descriptors - Spaltenbeschreiber
 */
    public List<ColumnDescriptor> getColumnDescriptors() {
        List<ColumnDescriptor> descriptors = new ArrayList();
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
 * Falls der Descriptor dem übergebenem String entspricht, wird dieser zurückgegeben.
 * @param columnName - Name der Spalten
 * @return descriptor - wie oben beschrieben
 * 		   null - andernfalls
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
