package com.sedico.generictableadapter;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
/**
 * Diese Klasse implementiert die Verbindung des Servers zur Datenbank
 */
public class DBConnection {
	/**
	 * Membervariablen
	 */
	private static Connection conn;
	
	/* TODO: add parameter to method to connect to different db servers and databases */
	/**
	 * Diese Methode stellt die Verbindung zur Datenbank her
	 * @return conn - Falls eine Verbindung hergestellt werden konnte, wird diese zurückgegeben,
	 * 				  Falls keine Verbindung hergestellt werden konnte wird null zurückgegeben.
	 */
	public static Connection connectToDB() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			String url = "jdbc:mysql://localhost:3306/SeDiCo";
			
			conn = DriverManager.getConnection(url,"root", "jenskohler");
		
			return conn;
			
		} 
		catch (ClassNotFoundException e) {
			e.printStackTrace();
			return null;
		} 
		catch (SQLException e) {	
			e.printStackTrace();
			return null;
		}
	}		
	/**
	 * Diese Methode schließt die Verbindung zur Datenbank wieder
	 */
	public void closeConnection() {
		try {
			conn.close();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}	
	/**
	 * Diese Methode gibt die Daten einer Tabelle zurück, die in der Datenbank gespeichert wurden
	 * @param database - Name der Datenbank
	 * @param table - Name der Tabelle
	 * @return rs - 
	 */
	public static ResultSet getTableMetaData(String database, String table) {
		ResultSet rs = null;
		String queryString = new String("SELECT * FROM " +
				"INFORMATION_SCHEMA.columns where table_schema='" + database + "'" +
						"and table_name='" + table + "';");
		try {
			Statement stmt = null;
			stmt = conn.createStatement();
			rs = stmt.executeQuery(queryString);
			
			return rs;
		} 
		catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		
		
	}
	
	
}
