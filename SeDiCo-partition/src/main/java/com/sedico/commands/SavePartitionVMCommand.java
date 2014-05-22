package com.sedico.commands;

import com.sedico.*;
import com.sedico.sql.SQLServers;
/**
 * Hier wird die Klasse SavePartitionVMCommand implementiert, welche das Interface ConsoleCommand implementiert.
 * Hier werden die Befehle implementiert um die VM zu speichern.
 * @author jens
 *
 */
public class SavePartitionVMCommand implements ConsoleCommand {
    /**
     * Diese Methode überschreibt die abstrakte Methode getKeys und liefert die Schlüssel als String zurück
     * @return new String[] { "saveVM" }
     */
	@Override
    public String[] getKeys() {
        return new String[] { "saveVM" };
    }
	/**
	 * Diese Methode überschreibt die abstrakte Methode getUsage und liefert den Nutzen als String zurück
	 * @return "saveVM [<AWS|Euca>] [<instanceID>] [<ConfigName>] [<MySQL|Oracle|Blackhole>] [<port>] [<database>] [<table>] [<user>] [<password>]\n" +
               "Speichert die Informationen zu einer VM in der Konfigurationsdatei.\n" +
               "Damit kann diese VM als Teil einer Partition benutzt werden."; - String, der zurückgeliefert wird
	 */
    @Override
    public String getUsage() {
        return "saveVM [<AWS|Euca>] [<instanceID>] [<ConfigName>] [<MySQL|Oracle|Blackhole>] [<port>] [<database>] [<table>] [<user>] [<password>]\n" +
               "Speichert die Informationen zu einer VM in der Konfigurationsdatei.\n" +
               "Damit kann diese VM als Teil einer Partition benutzt werden.";
    }
    /**
     * Diese Methode überschreibt die abstrakte Methode Execute und erstellt anhand der Befehle eine TargetConfiguratuion.
     * @param args - Befehle mit denen die Tabelle gefüllt wird
     */
    @Override
    public void Execute(String[] args) {
        if (args.length < 9) {
            System.out.println("Für diesen Befehl werden mindestens acht Parameter erwartet.");
            return;
        }

        int port = Integer.parseInt(args[4]);
        SQLServers serverType = SQLServers.MySQL;
        switch(args[3].toLowerCase()) {
            case "oracle":
                serverType = SQLServers.Oracle;
                break;
        }
        Configuration.addTargetConfiguration(args[2], "", port, args[5], args[6], args[7], args[8], serverType);
        System.out.println("Konfiguration erfolgreich hinzugefügt.");
        System.out.println("Bitte fügen Sie jetzt noch die Spalten hinzu, die verwendet werden sollen. Es wurde bereits eine Spalte sampleColumnName definiert.");
    }
}
