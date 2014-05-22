package com.sedico.partition;

import com.sedico.sql.*;
/**
 * Diese Klasse erzeugt eine partitionierte Tabelle.
 * @author jens
 *
 */
public class PartitionedTable {
    private final Table table;
    private final int partition;

    public PartitionedTable(Table table, int partition) {
        this.table = table;
        this.partition = partition;
    }

    public Table getTable() {
        return table;
    }

    public int getPartition() {
        return partition;
    }
}
