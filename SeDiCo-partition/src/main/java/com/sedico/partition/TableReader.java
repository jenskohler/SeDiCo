package com.sedico.partition;

import com.sedico.Configuration;
import com.sedico.sql.Table;
import com.sedico.sql.reading.*;
/**
 * Diese Klasse erzeugt einen Tabellenbeschreiber, anhand eines Spaltenbeschreibers. Diese Klasse beschreibt die Tabelle.
 * @author jens
 *
 */
public class TableReader {

    private final SQLReaderStrategy readerStrategy;

    public TableReader() {
        PartitionDescriptor partition = Configuration.getSecondaryPartition();
        readerStrategy = SQLReaderStrategyFactory.createSqlReaderStrategy(partition);
    }
    /**
     * In dieser Methode wird eine Tabelle angefragt 
     * @param table - Die Tabelle wird an die Anfrage übergeben
     * @return table - Die ausgewählte Tabelle 
     */
    public Table select(Table table) {
        table = readerStrategy.queryTable(table);
        return table;
    }
}
