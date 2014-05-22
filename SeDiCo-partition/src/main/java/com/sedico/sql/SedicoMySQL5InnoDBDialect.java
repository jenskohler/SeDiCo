package com.sedico.sql;

import org.hibernate.dialect.*;

import java.sql.Types;
/**
 * Diese Klasse setzt den Spaltentyp der MySQL-Datenbank fest. Sie erbt von der Klasse MySQL5InnoDBDialect.
 * @author jens
 *
 */
public class SedicoMySQL5InnoDBDialect extends MySQL5InnoDBDialect {
    /**
     * Der Konstruktor legt den Datentyp auf DECIMAL fest.
     */
	public SedicoMySQL5InnoDBDialect() {
        //aus unerklärlichen Gründen fehlt dieser Datentyp in Hibernate, was zu einer Exception führt,
        //daher hier manuell hinzugefügt
        super();
    	registerColumnType(Types.DECIMAL, "decimal($p,$s)");
        
        
    }
}
