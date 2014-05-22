package com.sedico;

import com.sedico.commands.*;

import java.util.*;
/**
 * Diese Klasse implementiert die Ausstattung der Konsole.
 * @author jens
 *
 */
public class ConsoleEnvironment {

    private static final Map<String, ConsoleCommand> commands = new HashMap();
    /**
     * Diese Methode fügt in einer Map zu einem String ein Konsolekommando als Wert hinzu, falls Map leer ist.
     * @return commands - die gefüllte Map wird zurückgegeben
     */
    public static Map<String, ConsoleCommand> getCommands() {
        if (commands.isEmpty()) {
            for(ConsoleCommand command : getCommandsList()) {
                for(String key : command.getKeys()) {
                    commands.put(key, command);
                }
            }
        }
        return commands;
    }
    /**
     * Diese Methode liefert die Kommandos in Form einer Liste zurück
     * @return ConsoleCommand[] - Ein Array von KonsolenKommandos
     */
    public static ConsoleCommand[] getCommandsList() {
        //HACK: Es scheint in Java nicht möglich zu sein zuverlässig alle Klassen per Reflection zu laden, die ein Interface implementieren
        //wenn doch, dann das hier bitte ändern
        return new ConsoleCommand[] {
                new ExitCommand(),
                new HelpCommand(),
                new PartitionCommand(),
                new SavePartitionVMCommand()
        };
    }
}
