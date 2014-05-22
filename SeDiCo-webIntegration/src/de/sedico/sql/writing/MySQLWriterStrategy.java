package de.sedico.sql.writing;

import de.sedico.partition.*;
import de.sedico.sql.MySQLBuilder;
import de.sedico.sql.SQLBuilder;
/**
 * Diese Klasse impelementiert die Schreibstrategie der MySQL-Datenbank. Sie erbt von der SQLWriterStrategyBase, welche die Basis der Schreibstrategie implementiert.
 * @author jens
 *
 */
public class MySQLWriterStrategy extends SQLWriterStrategyBase {

    public MySQLWriterStrategy(PartitionDescriptor settings) {
        super(settings);
    }
    /**
     * Diese Methode erzeugt einen SQLBuilder der MySQL-Datenbank.
     * @return new MySQLBuilder() - SQLBuilder der MYSQL-Datenbank
     */
    @Override
    protected SQLBuilder createSQLBuilder() {
        return new MySQLBuilder();
    }
}
