package com.sedico.generictableadapter;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class GenericTableAdapter {
	private String 		tableName;
	private int 		columnCount;
	private int 		rowCount;
	private ColumnType 	columnType;
	
	private static Connection 
		connection = DBConnection.connectToDB();
	
	
	public static void main (String[] args) {
		
		ResultSet rs;
		try {
			Statement stmt = connection.createStatement();
			
			
			rs = stmt.executeQuery("SELECT * FROM Customer;");
			while(rs.next()){
				int id = rs.getInt("Id");
				String forename = rs.getString("Forename");
				
				System.out.println("\tId= " + id + "\tForename = " + forename);		
			}
		} 
		catch (SQLException e) {
			e.printStackTrace();
		}
		
		
	}
}
