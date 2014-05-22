package de.sedico.partition;

import de.sedico.sql.*;
/**
 * Diese Klasse erstellt die Tabelle in einer Partition.
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
