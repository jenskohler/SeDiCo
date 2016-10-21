package com.sedico.sql;

import java.util.*;
/**
 * Dieses Interface bildet die Grundlage für das Erzeugen, Einfügen, Löschen, Aktualisieren von Tabellen ist.
 * @author jens
 *
 */
public interface SQLBuilder {
    String buildCreateTable(Table table, String databaseName,boolean isPrimaryPartition);

    List<String> buildInserts(Table table, String databaseName);

    List<String> buildDeletes(Table table, String databaseName);

    List<String> buildUpdates(Table table, String databaseName);

    String buildSelect(Table table, String databaseName);
}
