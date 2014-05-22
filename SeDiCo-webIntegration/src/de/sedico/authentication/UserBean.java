package de.sedico.authentication;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.naming.NamingException;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import de.sedico.generictableadapter.DBConnection;


@ManagedBean
/**
 * Diese Klasse implementiert die Informationen eines Nutzers.
 * @author jens
 *
 */
public class UserBean implements Serializable {
	private static final long serialVersionUID = 1L;
	private static Connection con;
	
	@Id
	String 		username;
	String		password;

	public UserBean(String username, String password) {
			this.username = username;
			this.password = password;
		
			
			this.create();
	}

	public UserBean() {
		
	}
	
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	/**
	 * Diese Methode erzeugt eine Verbindung zur Datenbank.
	 * @return index.xhtml
	 */
	public String create() {
		con = UserBean.connectToDB();
		try {
		
			 Statement st = con.createStatement();
			 String insertString = "INSERT INTO Users (Username, Password) VALUES (" +
					   "\"" + this.getUsername() + "\", MD5( \"" +
					   this.getPassword() + "\" ));";
			 	 
			System.out.println(insertString);
			 
			 String insertRole = "INSERT INTO Roles (Username, Rolename) VALUES (\"" + this.getUsername() + "\", \"Admin\" );";
			 
			 st.executeUpdate(insertRole);
			 st.executeUpdate(insertString);
			 
			 con.close();
	 
	  
		}
		catch (SQLException s){
			s.getMessage();
			s.printStackTrace();
			
		}
		return "index.xhtml";

	}
	
	
	/**
	 * Diese Methode stellt die Verbindung zur Datenbank her
	 * @return conn - Falls eine Verbindung hergestellt werden konnte, wird diese zurückgegeben, ansonsten null.
	 */
	public static Connection connectToDB() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			String url = "jdbc:mysql://localhost:3306/SeDiCo";
			
			con = DriverManager.getConnection(url,"root", "jenskohler");
		
			return con;
			
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
	
	
}
