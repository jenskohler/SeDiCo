package de.sedico.partition;

import com.sedico.*;
import de.sedico.sql.*;
import de.sedico.sql.writing.SQLWriterStrategy;
import de.sedico.sql.writing.SQLWriterStrategyFactory;

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
 * Diese Methode erzeugt eine Tabelle
 * @param table - Tabelle
 */
    public void createTable(Table table) {
        checkSanity(table);

        for (SQLWriterStrategy strategy : exportStrategies) {
            strategy.createTable(table);
        }
    }
    /**
     * Diese Methode fügt Inhalte in die Tabelle ein.
     * @param table - Tabelle
     */
    public void insert(Table table) {
        checkSanity(table);

        Iterable<PartitionedTable> tables = tableStrategy.getPartitionedTables(table);
        for (PartitionedTable partitionedTable : tables) {
            SQLWriterStrategy strategy = exportStrategies.get(partitionedTable.getPartition());
            strategy.insertTable(partitionedTable.getTable());
        }
    }
/**
 * Diese Methode löscht Inhalte in der Tabelle
 * @param table - Tabelle
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
 * Diese Methode aktualisiert die Tabelle
 * @param table - Tabelle
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
     * Diese Methode überprüft die Plausibilität einer Tabelle.
     * @param table - Tabelle
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
