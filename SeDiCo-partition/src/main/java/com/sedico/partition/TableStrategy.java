package com.sedico.partition;

import com.sedico.sql.*;
/**
 * Dieses Interface beschreibt die Strategie einer Tabelle.
 * @author jens
 *
 */
public interface TableStrategy {
    Iterable<PartitionedTable> getPartitionedTables(Table table);
}
