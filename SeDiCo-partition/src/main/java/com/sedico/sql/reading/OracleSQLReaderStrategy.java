package com.sedico.sql.reading;

import com.sedico.partition.PartitionDescriptor;
import com.sedico.sql.*;
/**
 * Hier wird die Klasse OracleSQLReaderStrategy implementiert, welche von der Klasse SQLReaderStrategyBase erbt.
 * Die Klasse implemtiert die Lesestrategie der Oracle-Datenbank.
 * @author jens
 *
 */
public class OracleSQLReaderStrategy extends SQLReaderStrategyBase {
    public OracleSQLReaderStrategy(PartitionDescriptor partition) {
        super(partition);
    }
    /**
     * Diese Methode erzeugt den SQL-Builder.
     * @return new OracleSQLBuilder() - Erzeuger der Oracle-Datenbank
     */
    @Override
    protected SQLBuilder createSQLBuilder() {
        return new OracleSQLBuilder();
    }
}
