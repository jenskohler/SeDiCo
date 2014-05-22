package com.sedico.sql.writing;

import com.sedico.partition.*;
import com.sedico.sql.OracleSQLBuilder;
import com.sedico.sql.SQLBuilder;
/**
 * Diese Klasse implementiert die Klasse OracleSQLWriterStrategy und erbt von der Klasse OracleSQLWriterStrategy.
 * Die Klasse implementiert die Schreibstrategie der Oracle-Datenbank.
 * @author jens
 *
 */
public class OracleSQLWriterStrategy extends SQLWriterStrategyBase {

    public OracleSQLWriterStrategy(PartitionDescriptor settings) {
        super(settings);
    }
    /**
     * Diese Methode erzeugt einen SQLBuilder.
     * @return new OracleSQLBuilder() - Erzeuger der Oracle-Datenbank
    */
    @Override
    protected SQLBuilder createSQLBuilder() {
        return new OracleSQLBuilder();
    }
}
