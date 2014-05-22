package com.sedico.partition;

import com.sedico.sql.*;

import java.util.*;

/**
 * Diese Klasse partitioniert eine ganze Tabelle einer SQL-Datenbank und implementiert das Interface TableStrategy.
 */
public class SQLTableStrategy implements TableStrategy {

    private final PartitionerStrategy partitioner;

    public SQLTableStrategy(PartitionerStrategy partitioner) {
        this.partitioner = partitioner;
    }
    /**
     * Diese Methode liefert alle partitionierten Tabellen zurück, welche in einer Map gespeichert werden.
     * @param table - Es wird die Tabelle übergeben, welche partitioniert wird
     * @return createPartitionedTables(table, tableRows) - das Iterable 
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
     * Diese Methode durchläuft die Partionen und fügt einer Map die aktuelle Nummer der Partition als Schlüssel und eine Array List als Wert hinzu.
     * @return tableRows - Die Map mit dem Schlüssel currentPartition und Wert new ArrayList()

     */
    private HashMap<Integer, List<Row>> createHashMapForPartitions() {
        HashMap<Integer, List<Row>> tableRows = new HashMap();
        for (int currentPartition = 0; currentPartition < partitioner.getTotalPartitions(); currentPartition++) {
            tableRows.put(currentPartition, new ArrayList());
        }
        return tableRows;
    }
    /**
     * Diese Methode erstellt die partitionierte Tabelle und fügt diese Tabellen einer Liste hinzu.
     * @param table - Es wird die Tabelle übergeben, die partitioniert werden soll
     * @param tableRows - Map, als Schlüssel Integer und als Wert List<Row>
     * @return tables - Liste der partitionierten Tabellen
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
