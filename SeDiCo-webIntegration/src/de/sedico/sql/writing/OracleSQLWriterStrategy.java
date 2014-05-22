package de.sedico.sql.writing;

import de.sedico.partition.*;
import de.sedico.sql.OracleSQLBuilder;
import de.sedico.sql.SQLBuilder;
/**
 * Diese Klasse impelementiert die Schreibstrategie der Oracle-Datenbank. Sie erbt von der SQLWriterStrategyBase, welche die Basis der Schreibstrategie implementiert.
 * @author jens
 *
 */
public class OracleSQLWriterStrategy extends SQLWriterStrategyBase {

    public OracleSQLWriterStrategy(PartitionDescriptor settings) {
        super(settings);
    }
/**
 * Diese Methode erzeugt einen SQLBuilder der Oracle-Datenbank.
 * @return new OracleSQLBuilder() - SQLBuilder der Oracle-Datenbank
 */
    @Override
    protected SQLBuilder createSQLBuilder() {
        return new OracleSQLBuilder();
    }
}
