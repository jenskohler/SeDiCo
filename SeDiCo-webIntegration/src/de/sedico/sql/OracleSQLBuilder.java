package de.sedico.sql;

import org.hibernate.dialect.*;
/**
 * Diese Klasse stellt den Erzeuger der Oracle-Datenbank dar. Sie implementiert das Interface SQLBuilderBase.
 * @author jens
 *
 */
public class OracleSQLBuilder extends SQLBuilderBase {

    private Dialect dialect = new Oracle10gDialect();

    @Override
    protected Dialect getDialect() {
        return dialect;
    }
}
