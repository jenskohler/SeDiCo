package de.sedico.partition;

import de.sedico.sql.*;
/**
 * Dieses Interface implementiert die Strategie eine Partition zu erstellen.
 * @author jens
 *
 */
public interface PartitionerStrategy {
    int getTotalPartitions();
    Iterable<Column> getColumnsForPartition(int currentPartition, Table table, Row row);
}
