package com.sedico.commands;

import com.sedico.partition.*;
import com.sedico.sql.*;
import com.sedico.sql.importing.*;
/**
 * Hier wird die Klasse PartitionCommand implementiert, welche das Interface ConsoleCommand implementiert.
 * Hier werden die Befehle für die Nutzung der Partition implementiert.
 * @author jens
 *
 */
public class PartitionCommand implements ConsoleCommand {
    /**
     * Diese Methode überschreibt die abstrakte Methode getKeys und liefert die Schlüssel zurück.
     * @return String[] - Schlüssel als String-Array
     */
	@Override
    public String[] getKeys() {
        return new String[] { "partition", "p" };
    }
	/**
	 * Diese Methode überschreibt die abstrakte Methode getUsage und liefert den Nutzen der Partitionsbefehle als String zurück
	 * @return "partition - Benutzt die aktuelle Konfiguration, um die bestehende Tabelle zu partitionieren.";

	 */
    @Override
    public String getUsage() {
        return "partition - Benutzt die aktuelle Konfiguration, um die bestehende Tabelle zu partitionieren.";
    }
    /**
     * Diese Methode überschreibt die abstrakte Methode Execute und erzeugt eine Tabelle in die Inhalte geschrieben werden.
     * @param args - Befehle
     */
    @Override
    public void Execute(String[] args) {

        System.out.println("Daten aus Quelltabelle werden abgerufen.");

        SQLImportStrategy strategy = new SQLImportStrategyBase();
        Table table = strategy.fetchTable();
        TableWriter writer = new TableWriter();

        System.out.println("Zieltabellen werden erstellt.");
        writer.createTable(table);

        System.out.println("Daten werden in Zieltabellen geschrieben");
        writer.insert(table);
    }
}
