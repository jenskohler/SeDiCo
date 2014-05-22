package de.sedico.test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.sedico.generictableadapter.DBConnection;

public class TestDBConnection {
	private static Connection conn = DBConnection.connectToDB();
	private static Statement stmt = null;
	
	public static void main (String[] args) {
		
		ResultSet rs;
		try {
			stmt = conn.createStatement();
			rs = stmt.executeQuery("SELECT * FROM Customer;");
			while(rs.next()){
				int id = rs.getInt("Id");
				String forename = rs.getString("Forename");
				
				System.out.println("Id= " + id + "\t\t\tForename = " + forename);
			}
			rs = null;
			rs = DBConnection.getTableMetaData("SeDiCo", "Customer");
			while (rs.next()) {
				String columnName = rs.getString("column_name");
				String columnType = rs.getString("data_type");
				
				System.out.println("Name= " + columnName + "\t\t\t\t\tType = " + columnType);
				
			}
			
			rs = null;
			stmt = conn.createStatement();
			rs = stmt.executeQuery("mysqldump -uroot -pjenskohler SeDiCo Customer ;");
			while (rs.next()) {
				System.out.println(rs.next());
			}
			
			conn.close();
		} 
		catch (SQLException e) {
			e.printStackTrace();
			
		}		
	}
}
