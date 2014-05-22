package de.sedico.partition;

import de.sedico.sql.*;

import java.util.*;

/**
 * Diese Klasse partitioniert eine ganze Tabelle einer SQL-Datenbank. Sie implementiert das Interface TableStrategy.
 */
public class SQLTableStrategy implements TableStrategy {

    private final PartitionerStrategy partitioner;

    public SQLTableStrategy(PartitionerStrategy partitioner) {
        this.partitioner = partitioner;
    }
/**
 * Diese Methode liefert die partitionierten Tabellen zurück.
 * @param table - zu partitionierende Tabelle
 * @return createPartitionedTables(table, tableRows) - die partionierten Tabellen
 */
    public Iterable<PartitionedTable> getPartitionedTables(Table table) {
        HashMap<Integer, List<Row>> tableRows = createHashMapForPartitions();

        for (Row row : table.getRows()) {
            for (int currentPartition = 0; currentPartition < partitioner.getTotalPartitions(); currentPartition++) {
                List<Row> rows = tableRows.get(currentPartition);
                List<Column> columns = new ArrayList();
                for(Column column : partitioner.getColumnsForPartition(currentPartition, table, row)) {
                    columns.add(new Column(column.getColumnName(), column.getColumnValue()));
                }
                Row newRow = new Row(columns);
                rows.add(newRow);
            }
        }
        return createPartitionedTables(table, tableRows);
    }
/**
 * Diese Methode speichert anhand eines Integers eine Liste von Spalten und gibt sie als Map zurück.
 * @return tableRows - HashMap<Integer, List<Row>>
 */
    private HashMap<Integer, List<Row>> createHashMapForPartitions() {
        HashMap<Integer, List<Row>> tableRows = new HashMap();
        for (int currentPartition = 0; currentPartition < partitioner.getTotalPartitions(); currentPartition++) {
            tableRows.put(currentPartition, new ArrayList());
        }
        return tableRows;
    }
    /**
     * Diese Methode erzeugt die partitionierten Tabellen.
     * @param table - Tabelle
     * @param tableRows - HashMap mit Schlüssel Integer und Wert HashMap<Integer, List<Row>>
     * @return tables - Liste von partitionierten Tabellen
     */
    private Iterable<PartitionedTable> createPartitionedTables(Table table, HashMap<Integer, List<Row>> tableRows) {
        List<PartitionedTable> tables = new ArrayList();
        for (Integer currentPartition : tableRows.keySet()) {
            List<Row> rows = tableRows.get(currentPartition);
            Table newTable = new Table(table.getTableName(), rows, table.getColumnDescriptors());
            PartitionedTable partitionedTable = new PartitionedTable(newTable, currentPartition);
            tables.add(partitionedTable);
        }
        return tables;
    }
}
