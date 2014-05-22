package de.sedico.sql;

import java.util.*;
/**
 * Hier wird das Interface SQLBuilder implementiert, welche einen Erzeuger einer SQL-Datenbank darstellt.
 * @author jens
 *
 */
public interface SQLBuilder {

	String buildCreateTable(Table table, String databaseName);

    List<String> buildInserts(Table table, String databaseName);

    List<String> buildDeletes(Table table, String databaseName);

    List<String> buildUpdates(Table table, String databaseName);
    
    List<String> buildDrops(Table table, String databaseName);

    String buildSelect(Table table, String databaseName);
}
