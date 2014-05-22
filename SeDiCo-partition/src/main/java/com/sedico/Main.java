package com.sedico;

import com.sedico.commands.ConsoleCommand;

import java.util.Arrays;
import java.util.Scanner;
/**
 * Diese Klasse implementiert die Main.
 * @author jens
 *
 */
public class Main {
	/**
	 * Diese Methode durchläuft die Eingabe und prüft, ob der Command null ist.
	 * @param args - Array von Strings
	 */
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        while(true) {
            System.out.print("> ");
            String line = scanner.nextLine();
            if (line == null || line.equals("")) {
                continue;
            }
            String[] parts = line.split("\\s+");
            ConsoleCommand command = ConsoleEnvironment.getCommands().get(parts[0]);
            if (command == null) {
                System.out.printf("Der Befehl %s konnte nicht gefunden werden.\n", parts[0]);
            }
            else {
                command.Execute(Arrays.copyOfRange(parts, 1, parts.length));
            }
        }
    }
}