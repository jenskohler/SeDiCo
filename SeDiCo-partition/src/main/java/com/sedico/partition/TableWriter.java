package com.sedico.partition;

import com.sedico.*;
import com.sedico.sql.*;
import com.sedico.sql.writing.SQLWriterStrategy;
import com.sedico.sql.writing.SQLWriterStrategyFactory;

import java.util.*;
/**
 * Diese Klasse schreibt eine Tabelle.
 * @author jens
 *
 */
public class TableWriter {

    private final TableStrategy tableStrategy;
    private final List<SQLWriterStrategy> exportStrategies;

    public TableWriter() {
        List<PartitionDescriptor> partitions = Configuration.getPartitions();
        exportStrategies = SQLWriterStrategyFactory.createSqlWriterStrategies(partitions);
        PartitionerStrategy partitioner = new PartitionByColumnNamesStrategy(partitions);
        tableStrategy = new SQLTableStrategy(partitioner);
    }
    /**
     * Diese Methode erzeugt für alle vorhandenen SQL-Schreibestrategien jeweils eine Tabelle.
     * @param table - Tabelle 
     */
    public void createTable(Table table) {
        checkSanity(table);

        for (SQLWriterStrategy strategy : exportStrategies) {
            strategy.createTable(table);
        }
    }
    /**
     * Die Methode fügt Inhalte in eine partionierte Tabelle ein
     * @param table - In diese Tabelle werden Inhalte eingefügt
     */
    public void insert(Table table) {
        checkSanity(table);

        Iterable<PartitionedTable> tables = tableStrategy.getPartitionedTables(table);
        //System.out.println("Table Rows: " +table.getRows().size());
//        for (Row row : table.getRows()) {
//            for (Column column : row.getColumns()) {
//            	//System.out.println(column.getColumnName() + column.getColumnValue());
//            }
//        }
        for (PartitionedTable partitionedTable : tables) {
            SQLWriterStrategy strategy = exportStrategies.get(partitionedTable.getPartition());
            strategy.insertTable(partitionedTable.getTable());
        }
    }
    /**
     * Diese Methode löscht eine partitionierte Tabelle
     * @param table - Diese Tabelle wird gelöscht
     */
    public void delete(Table table) {
        checkSanity(table);

        Iterable<PartitionedTable> tables = tableStrategy.getPartitionedTables(table);
        for (PartitionedTable partitionedTable : tables) {
            SQLWriterStrategy strategy = exportStrategies.get(partitionedTable.getPartition());
            strategy.deleteTable(partitionedTable.getTable());
        }
    }
    /**
     * Diese Methode aktualisiert die Inhalte einer Tabelle.
     * @param table - Die zu aktualisierende Tabelle 
     */
    public void update(Table table) {
        checkSanity(table);

        Iterable<PartitionedTable> tables = tableStrategy.getPartitionedTables(table);
        for (PartitionedTable partitionedTable : tables) {
            SQLWriterStrategy strategy = exportStrategies.get(partitionedTable.getPartition());
            strategy.updateTable(partitionedTable.getTable());
        }
    }
    /**
     * Diese Methode prüft, ob mehr Exportstrategien als Tabellenzeilen vorhanden sind. Falls dies zutrifft wird eine IllegalStateException geworfen.
     * @param table - die zu bearbeitende Tabelle wird übergeben
     */
    private void checkSanity(Table table) {
        if (table.getRows().size() < exportStrategies.size() - 1) {
            throw new IllegalStateException("Sie haben mehr Exportstrategien als Tabellenspalten. " +
                    "Das ist nicht vorgesehen, denn mit dieser Konfiguration würden einige leere Tabellen entstehen.");
        }
        if (exportStrategies.size() < 2) {
            throw new IllegalStateException("Sie müssen mindestens zwei Exportstrategien angeben. Die Benutzung einer einzigen Strategie hat keinen Nutzen.");
        }
    }
}
