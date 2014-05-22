package com.sedico.sql.reading;

import com.sedico.sql.Table;
/**
 * Diese Klasse implementiert das Interface SQLReaderStrategy. Hier wird die Lesestrategie der SQL-Datenbank impelmentiert.		
 * @author jens
 *
 */
public interface SQLReaderStrategy {
    Table queryTable(Table table);
}
