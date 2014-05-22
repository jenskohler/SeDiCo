package de.sedico.sql.writing;

import de.sedico.sql.*;
/**
 * Hier wird das Interface SQLWriterStrategy implementiert. Es beschreibt wie eine Tabelle erzeugt, Inhalte eingefügt oder gelöscht werden.
 * @author jens
 *
 */
public interface SQLWriterStrategy {
    void createTable(Table table);

    void insertTable(Table table);

    void deleteTable(Table table);
    
    void dropTable(Table table);

    void updateTable(Table table);
}
