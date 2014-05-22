package de.sedico.sql;

import org.hibernate.dialect.*;
/**
 * Diese Klasse stellt den Erzeuger der MySQL-Datenbank dar. Sie implementiert das Interface SQLBuilderBase.
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
