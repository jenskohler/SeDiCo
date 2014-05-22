package com.sedico.sql.writing;

import com.sedico.partition.*;

import java.util.*;
/**
 * Diese Klasse implementiert die Klasse SQLWriterStrategyFactory. Sie fertigt die Schreibstrategie einer SQL-Datenbank an.
 * @author jens
 *
 */
public class SQLWriterStrategyFactory {
	/**
	 * Diese Methode erzeugt eine Liste von Datenbankschreiber.
	 * @param partitions - Spaltenbeschreiber
	 * @return strategies - Liste der SQLWriterStrategy
	 */
    public static List<SQLWriterStrategy> createSqlWriterStrategies(List<PartitionDescriptor> partitions) {
        List<SQLWriterStrategy> strategies = new ArrayList();
        for(PartitionDescriptor partition : partitions) {
            strategies.add(createSqlWriterStrategy(partition));
        }
        return strategies;
    }
		/**
		 * Diese Methode erzeugt eine Schreibstrategie des Datenbanktyps.
		 * @param partition - Spaltenbeschreiber
		 * @return new OracleSQLWriterStrategy
		 */
	    public static SQLWriterStrategy createSqlWriterStrategy(PartitionDescriptor partition) {
        switch(partition.getServerType()) {
            case Oracle:
                return new OracleSQLWriterStrategy(partition);
            case MySQL:
                return new MySQLWriterStrategy(partition);
        }
        throw new Error("Es gibt keine WriteStrategy für diesen SQL-Server-Typ.");
    }
}
