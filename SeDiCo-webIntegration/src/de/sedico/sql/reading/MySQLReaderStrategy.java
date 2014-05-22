package de.sedico.sql.reading;

import de.sedico.partition.PartitionDescriptor;
import de.sedico.sql.*;
/**
 * Diese Klasse implementiert die Lesestrategie der MySQL-Datenbank. Diese Klasse implementiert die SQLReaderStrategyBase.
 * @author jens
 *
 */
public class MySQLReaderStrategy extends SQLReaderStrategyBase {
    public MySQLReaderStrategy(PartitionDescriptor partition) {
        super(partition);
    }
/**
 * Diese Methode erzeugt eine SQLBuilder.
 * @return new MySQLBuilder - Erzeuger der MySQL-Datenbank
 */
    @Override
    protected SQLBuilder createSQLBuilder() {
        return new MySQLBuilder();
    }
}
