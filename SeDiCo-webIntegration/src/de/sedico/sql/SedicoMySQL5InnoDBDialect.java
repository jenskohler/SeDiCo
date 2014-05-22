package de.sedico.sql;

import org.hibernate.dialect.*;

import java.sql.Types;
/**
 * Diese Klasse übersetzt die MySQL-Datenbank in einen Datenbank-Dialekt. Die Klasse erbt von der Klasse MySQL5InnoDBDialect.
 * @author jens
 *
 */
public class SedicoMySQL5InnoDBDialect extends MySQL5InnoDBDialect {
    public SedicoMySQL5InnoDBDialect() {
        //aus unerklärlichen Gründen fehlt dieser Datentyp in Hibernate, was zu einer Exception führt,
        //daher hier manuell hinzugefügt
        registerColumnType(Types.DECIMAL, "decimal($p,$s)");
    }
}
