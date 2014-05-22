package com.sedico.commands;
/**
 * Diese Klasse implementiert die Klasse ExitCommand und implementiert das Interface ConsoleCommand.
 * Hier werden die Befehle für das Beenden der Konsole implementiert.
 * @author jens
 *
 */
public class ExitCommand implements ConsoleCommand {
	/**
	 * Diese Methode überschreibt die abstrakte Methode getKeys und liefert die Schlüssel zurück
	 * @return String[] - die Schlüssel werden als Array zurückgegeben
	 */
    @Override
    public String[] getKeys() {
        return new String[] { "exit", "quit", "q" };
    }
    /**
     * Diese Methode überschreibt die abstrakte Methode getUsage und gibt einen String zurück
     * @return "exit - Beendet diese Konsole";

     */
    @Override
    public String getUsage() {
        return "exit - Beendet diese Konsole";
    }
    /**
     * Diese Methode überschreibt die abstrakte Methode Execute und beendet das Programm.
     * @param args - String-Array
     */
    @Override
    public void Execute(String[] args) {
       System.exit(0);
    }
}
