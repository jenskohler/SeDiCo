package com.sedico.partition;

import com.sedico.sql.*;

/**
 * Diese Klasse implementiert das Interface PartitionerStrategy. Es beschreibt die Strategie, wie partitioniert wird.
 * @author jens
 *
 */
public interface PartitionerStrategy {
    int getTotalPartitions();
    Iterable<Column> getColumnsForPartition(int currentPartition, Table table, Row row);
}
