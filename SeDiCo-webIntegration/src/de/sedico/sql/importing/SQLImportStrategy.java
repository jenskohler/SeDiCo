package de.sedico.sql.importing;

import de.sedico.sql.Table;
/**
 * Hier wird das Interface SQLImportStrategy implementiert. Hier wird die Importstrategy der SQL-Datenbank implementiert.
 * @author jens
 *
 */
public interface SQLImportStrategy {
    Table fetchTable();
}
