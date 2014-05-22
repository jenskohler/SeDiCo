package de.sedico.partition;

import de.sedico.sql.*;
/**
 * Hier wird die Strategie einer Tabelle als Interface implementiert.
 * @author jens
 *
 */
public interface TableStrategy {
    Iterable<PartitionedTable> getPartitionedTables(Table table);
}
