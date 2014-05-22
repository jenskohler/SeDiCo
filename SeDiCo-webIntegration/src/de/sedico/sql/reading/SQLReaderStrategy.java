package de.sedico.sql.reading;

import de.sedico.sql.Table;
/**
 * Hier wird das Interface SQLReaderStrategy implementiert, welches für die Lesestrategie der SQL-Datenbank zuständig ist.
 * @author jens
 *
 */
public interface SQLReaderStrategy {
    Table queryTable(Table table);
}
