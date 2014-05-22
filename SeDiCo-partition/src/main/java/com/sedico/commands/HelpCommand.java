package com.sedico.commands;

import com.sedico.ConsoleEnvironment;
/**
 * Diese Klasse impelementiert die Klasse HelpCommand, die das Interface ConsoleCommand implementiert.
 * Hier werden die Befehle für die Hilfe implementiert.
 * @author jens
 *
 */
public class HelpCommand implements ConsoleCommand {
	/**
	 * Diese Methode überschreibt die abstrakte Methode getKeys und liefert die Schlüssel als String-Array zurück
	 * @return String[]
	 */
    @Override
    public String[] getKeys() {
        return new String[] { "help", "?", "h" };
    }
    /**
     * Diese Methode überschreibt die abstrakte Methode getUsage und liefert den Nutzen des Hilfsbefehls als String zurück
     * @return "help [<command>] - Zeigt die Hilfe für einen bestimmten Befehl.";

     */
    @Override
    public String getUsage() {
        return "help [<command>] - Zeigt die Hilfe für einen bestimmten Befehl.";
    }
    /**
     * Diese Methode überschreibt die abstrakte Methode Execute und liefert die Befehle zurück, falls diese korrekt sind.
     * @param args - Befehle
     */
    @Override
    public void Execute(String[] args) {
        if (args.length > 0) {
            ConsoleCommand command = ConsoleEnvironment.getCommands().get(args[0]);
            if (command == null) {
                System.out.printf("Der Befehl %s konnte nicht gefunden werden.\n", args[0]);
            }
            else {
                System.out.println(command.getUsage());
            }
        }
        else {
            for (ConsoleCommand command : ConsoleEnvironment.getCommandsList()) {
                System.out.println(command.getUsage());
                System.out.println();
            }
        }
    }
}
