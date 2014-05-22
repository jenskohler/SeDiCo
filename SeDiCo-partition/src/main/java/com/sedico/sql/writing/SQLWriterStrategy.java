package com.sedico.sql.writing;

import com.sedico.sql.*;
/**
 * Hier wird das Interface SQLWriterStrategy implementiert. Es implementiert die Schreibstrategie einer SQL-Datenbank.
 * @author jens
 *
 */
public interface SQLWriterStrategy {
    void createTable(Table table);

    void insertTable(Table table);

    void deleteTable(Table table);

    void updateTable(Table table);
}
