package de.sedico.tools;

import java.io.BufferedWriter;
import java.io.FileWriter;
import org.apache.log4j.Logger;


/**
 * Diese Klasse implementiert einen Dateischreiber des SeDiCo-Projektes.
 * @author jens
 *
 */
public class SeDiCoFileWriter {
	private static Logger log = Logger.getLogger(SeDiCoFileWriter.class);
	/**
	 * Diese Methode schreibt eine Datei.
	 * @param filename - Name der Datei
	 */
	public void writeFile (String filename) {
		
		try{
			// Create file 
			FileWriter fstream = new FileWriter(filename);
			
			BufferedWriter out = new BufferedWriter(fstream);
			
			out.write("Hello Java");
			//Close the output stream
			
			out.close();
		}
		catch (Exception e) {
			 log.info("Error: " + e.getMessage());
		}
	
		
	}
		
		
		
		
		
	
	
}
