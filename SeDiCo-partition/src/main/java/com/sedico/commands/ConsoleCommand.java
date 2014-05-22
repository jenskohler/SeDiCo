package com.sedico.commands;

/**
 * Diese Klasse implementiert das Interface ConsoleCommand, welches für die Befehle für die Console zuständig ist.
 * @author jens
 *
 */
public interface ConsoleCommand {
 
	String[] getKeys();

    String getUsage();
  
    void Execute(String args[]);
}
