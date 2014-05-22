package com.sedico.sql;

import org.hibernate.dialect.*;
/**
 * Diese Klasse implementiert die Erzeugung der Datenbankverbindung. Sie erbt von der Klasse SQLBuilderBase, welche die Basis für die Erzeugung stellt.
 * @author jens
 *
 */
public class MySQLBuilder extends SQLBuilderBase {

    private Dialect dialect = new SedicoMySQL5InnoDBDialect();

    @Override
    protected Dialect getDialect() {
        return dialect;
    }
}
