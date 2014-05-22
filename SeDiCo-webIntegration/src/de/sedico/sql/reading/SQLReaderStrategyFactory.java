package de.sedico.sql.reading;

import de.sedico.partition.PartitionDescriptor;

import java.util.*;
/**
 * Diese Klasse implementiert die SQLReaderStrategyFactory. Diese Klasse fertigt die Lesestrategie der SQL-Datenbank an.
 * @author jens
 *
 */
public class SQLReaderStrategyFactory {
/**
 * Diese Methode erzeugt die Schreibestrategie der Datenbank für jeden Partitionbeschreiber.
 * @param partitions - Liste von Partitionbeschreibern
 * @return strategies - ArrayList von SQL-Lesestrategien
 */
    public static List<SQLReaderStrategy> createSqlReaderStrategies(List<PartitionDescriptor> partitions) {
        List<SQLReaderStrategy> strategies = new ArrayList<SQLReaderStrategy>();
        for(PartitionDescriptor partition : partitions) {
            strategies.add(createSqlReaderStrategy(partition));
        }
        return strategies;
    }
    /**
     * Diese Methode erzeugt die Lesestrategie der Oracle oder MySQL-Datenbank.
     * @return OracleSQLReaderStrategy oder MySQLReaderStrategy - Lesestrategie der MySQL oder Oracle-Datenbank
     */
    public static SQLReaderStrategy createSqlReaderStrategy(PartitionDescriptor partition) {
        switch(partition.getServerType()) {
            case Oracle:
                return new OracleSQLReaderStrategy(partition);
            case MySQL:
                return new MySQLReaderStrategy(partition);
        }
        throw new Error("Dieser Servertyp hat keine passenden Reader-Strategie.");
    }
}
