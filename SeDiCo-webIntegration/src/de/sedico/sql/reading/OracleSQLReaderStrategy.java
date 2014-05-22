package de.sedico.sql.reading;

import de.sedico.partition.PartitionDescriptor;
import de.sedico.sql.OracleSQLBuilder;
import de.sedico.sql.SQLBuilder;

/**
 * Diese Klasse implementiert die Lesestrategie der Oracle-Datenbank. Sie implementiert die SQLReaderStrategyBase.
 * @author jens
 *
 */
public class OracleSQLReaderStrategy extends SQLReaderStrategyBase {
    public OracleSQLReaderStrategy(PartitionDescriptor partition) {
        super(partition);
    }
/**
 * Diese Methode erzeugt einen SQLBuilder.
 * @return new OracleSQLBuilder() - Erzeuger der SQL-Datenbank
 */
    @Override
    protected SQLBuilder createSQLBuilder() {
        return new OracleSQLBuilder();
    }
}
