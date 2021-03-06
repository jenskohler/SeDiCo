package com.sedico.generictableadapter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

public class LoadMySQLData {
	private static String dumpString = null;
	
	public static String dumpMySQLData(	String user, 
										String password, 
										String database, 
										String ... tableName) {
		
		dumpString = new String ("mysqldump -u" + user + " -p" + password + " " + database + " ");
		
		int i = 0;
		
		if (tableName.length > 0) {
			while (tableName.length != i) {
				dumpString += tableName[i] + " ";
				i++;
			}
		}
		dumpString += " --skip-comments";
		dumpString += " --compact";
		dumpString += " --create-options";
		
		return dumpString;
	}
	
	private static void cleanDump(String dump) {
		/* 
		 * Pattern p = Pattern.compile
		 
			String recognizeComments = "/*";
		
			dump.re
		*/
		
	}
	
	public static void main (String[] args) {
		String dump = dumpMySQLData("root", "jenskohler", "SeDiCo", "Customer");
		Runtime rt = Runtime.getRuntime();
		Process p;
		BufferedReader inBuffer = null;
		String returnConsole;
		PrintWriter out = new PrintWriter(System.out);
		
		try {
			p = rt.exec(dump);
			
			inBuffer = new BufferedReader(
			           new InputStreamReader(p.getInputStream()));
			while ((returnConsole = inBuffer.readLine()) != null) {
		          out.println(returnConsole); 
		          out.flush();
		        }		
		} 
		catch (IOException e) {
			e.printStackTrace();
		} 	
	}
}
