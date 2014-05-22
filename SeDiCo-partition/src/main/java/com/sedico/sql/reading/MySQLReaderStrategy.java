package com.sedico.sql.reading;

import com.sedico.partition.PartitionDescriptor;
import com.sedico.sql.*;
/**
 * Hier wird die Klasse MySQLReaderStrategy implementiert, welche von der Klasse SQLReaderStrategyBase erbt.
 * Die Klasse implementiert die Lesestrategie der MySQL-Datenbank.  
 * @author jens
 *
 */
public class MySQLReaderStrategy extends SQLReaderStrategyBase {
    public MySQLReaderStrategy(PartitionDescriptor partition) {
        super(partition);
    }
    /**
     * Diese Methode erzeugt den SQLBuilder.
     * @return new MySQLBuilder() - Erzeuger den SQL-Datenbank
     */
    @Override
    protected SQLBuilder createSQLBuilder() {
        return new MySQLBuilder();
    }
}
