package com.sedico.sql.importing;

import com.sedico.sql.Table;
/**
 * Diese Klasse implementiert das Interface SQLImportStrategy. Dieses Interface sorgt für die Importstrategie der SQL-Datenbank.
 * @author jens
 *
 */
public interface SQLImportStrategy {
    Table fetchTable();
}
