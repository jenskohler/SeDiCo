package com.sedico.partition;

import com.sedico.sql.*;

import java.util.*;

/**
 * Diese Klasse partitioniert eine einzelne Zeile einer Tabelle und implementiert das Interface PartitionerStrategy.
 */
public class PartitionByColumnNamesStrategy implements PartitionerStrategy {

    private final List<PartitionDescriptor> partitions;

    public PartitionByColumnNamesStrategy(List<PartitionDescriptor> partitions) {
        this.partitions = partitions;
    }

    @Override
    public int getTotalPartitions() {
        return partitions.size();
    }
/**
 * Diese Methode liefert die Spalten einer Partition zurück. 
 * @param currentPartition - Nummer der aktuellen Partition
 * @param table - Tabelle
 * @param row - Zeile
 * @return columnPartitions - Liste von Spalten
 */
    @Override
    public Iterable<Column> getColumnsForPartition(int currentPartition, Table table, Row row) {
        if (row == null) {
            throw new IllegalArgumentException("row cannot be null.");
        }
        if (table == null) {
            throw new IllegalArgumentException("table cannot be null.");
        }
        if (currentPartition > getTotalPartitions()) {
            throw new IllegalArgumentException("currentPartition cannot be greater than the total number of partitions.");
        }
        if (currentPartition < 0) {
            throw new IllegalArgumentException("currentPartition cannot be less than zero.");
        }

        List<Column> columnPartitions = new ArrayList();
        //zuerst alle PK-Columns hinzufügen
        for(ColumnDescriptor descriptor : table.getPrimaryColumnDescriptors()) {
            Column column = row.getColumn(descriptor.getColumnName());
            columnPartitions.add(column);
        }
        //jetzt einfach alle Spalten für diese Partition anhängen
        for(String columnName : partitions.get(currentPartition).getColumns()) {
            Column column = row.getColumn(columnName);
            //wenn eine Spalte partitioniert werden soll, die im Datensatz fehlt diese Spalte überspringen
            if (column != null) {
                columnPartitions.add(column);
            }
        }
        return columnPartitions;
    }
}