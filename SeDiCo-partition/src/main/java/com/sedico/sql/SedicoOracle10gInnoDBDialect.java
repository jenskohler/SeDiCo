package com.sedico.sql;

import org.hibernate.dialect.*;

import java.sql.Types;
/**
 * Diese Klasse setzt den Spaltentyp der Oracle-Datenbank fest. Sie erbt von der Klasse Oracle10gDialect.
 * @author jens
 *
 */
public class SedicoOracle10gInnoDBDialect extends Oracle10gDialect {
/**
 * Der Konstruktor legt die Datentypen auf DECIMAL, BOOLEAN, BIT.
 */
	public SedicoOracle10gInnoDBDialect() {
    	super();
    	//aus unerklärlichen Gründen fehlt dieser Datentyp in Hibernate, was zu einer Exception führt,
        //daher hier manuell hinzugefügt
    	//System.out.println("REGISTERING ORACLE COLUMNTYPES");
    	
    	registerColumnType(Types.DECIMAL, "decimal($p,$s)");
    	registerColumnType(Types.BOOLEAN, "number(1, 0)");
    	registerColumnType(Types.BIT, "number(1, 0)");

       
        
        
        
    }
}
