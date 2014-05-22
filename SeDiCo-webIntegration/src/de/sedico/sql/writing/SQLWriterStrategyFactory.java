package de.sedico.sql.writing;

import de.sedico.partition.*;

import java.util.*;
/**
 * In dieser Klasse wird die Schreibestrategie der SQL-Datenbank angefertigt. 
 * @author jens
 *
 */
public class SQLWriterStrategyFactory {
/**
 * Die Methode erzeugt die Schreibestrategien.
 * @param partitions - Liste von PartitionDescriptor
 * @return strategies - Liste von SQLWriterStrategy 
 */
    public static List<SQLWriterStrategy> createSqlWriterStrategies(List<PartitionDescriptor> partitions) {
        List<SQLWriterStrategy> strategies = new ArrayList<SQLWriterStrategy>();
        for(PartitionDescriptor partition : partitions) {
            strategies.add(createSqlWriterStrategy(partition));
        }
        return strategies;
    }
    /**
     * Diese Methode legt fest, welche Schreibestrategie erzeugt wird.
     * @param partition - Spaltenbeschreiber
     * @return OracleSQLWriterStrategy oder MySQLWriterStrategy
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
