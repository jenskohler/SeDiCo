package com.sedico.sql;

import org.hibernate.dialect.*;
/**
 * Diese Klasse erzeugt die Verbindung zur Oracle-Datenbank. Sie erbt von der Klasse SQLBuilderBase, welche die Grundlage für die Erzeugung bildet.
 * @author jens
 *
 */
public class OracleSQLBuilder extends SQLBuilderBase {

    private Dialect dialect = new SedicoOracle10gInnoDBDialect();

    @Override
    protected Dialect getDialect() {
        return dialect;
    }
}
