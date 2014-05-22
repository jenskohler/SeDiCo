package com.sedico.sql.writing;

import com.sedico.partition.*;
import com.sedico.sql.MySQLBuilder;
import com.sedico.sql.SQLBuilder;
/**
 * Hier wird Klasse MySQLWriterStrategy implementiert, welche von der Klasse SQLWriterStrategyBase erbt.
 * Die Klasse implementiert die Schreibstrategie der MySQL-Datenbank. 
 * @author jens
 *
 */
public class MySQLWriterStrategy extends SQLWriterStrategyBase {
	
    public MySQLWriterStrategy(PartitionDescriptor settings) {
        super(settings);
    }
    /**
     * Diese Methode erzeugt einen SQLBuilder.
     * @return new MySQlBuilder() - Erzeuger der MySQL-Datenbank
     */
    @Override
    protected SQLBuilder createSQLBuilder() {
        return new MySQLBuilder();
    }
}
