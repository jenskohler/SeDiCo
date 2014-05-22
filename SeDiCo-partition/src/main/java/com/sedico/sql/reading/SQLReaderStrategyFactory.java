package com.sedico.sql.reading;

import com.sedico.partition.PartitionDescriptor;

import java.util.*;
/**
 * Hier wird die Klasse SQLReaderStrategyFactory implementiert. Die Klasse fertigt die Lesestrategie der SQL-Datenbank an.
 * @author jens
 *
 */
public class SQLReaderStrategyFactory {
	/**
	 * Diese Methode fügt die SQL-Lesestrategie für jeden Spaltenbeschreiber einer Liste hinzu.
	 * @param partitions - Liste von SQLReaderStrategy
	 * @return strategies - Liste von SQL-Lesestrategien
	 */
    public static List<SQLReaderStrategy> createSqlReaderStrategies(List<PartitionDescriptor> partitions) {
        List<SQLReaderStrategy> strategies = new ArrayList();
        for(PartitionDescriptor partition : partitions) {
            strategies.add(createSqlReaderStrategy(partition));
        }
        return strategies;
    }
    /**
     * Diese Methode erzeugt die SQL-Lesestrategie.
     * @param partition - Spaltenbeschreiber
     * @return new OracleSQLReaderStrategy oder MySQLReaderStrategy - Lesestrategie des Datenbanktypen
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
