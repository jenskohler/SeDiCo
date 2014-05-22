package de.sedico.generictableadapter;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import de.sedico.generictableadapter.DBConnection;
import de.sedico.partition.Configuration;
import de.sedico.partition.SQLServers;

import javax.faces.bean.SessionScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.log4j.Logger;

@ManagedBean(name = "dbConnection")
@SessionScoped
/**
 * Diese Klasse stellt die Verbindung zur Datenbank her.
 * @author jens
 *
 */
public class DBConnection {
	private static Logger log = Logger.getLogger(DBConnection.class);
	private String host;
	private String port;
	private String database;
	private String user;
	private String password;
	private String sqlType;

	public String getSqlType() {
		return sqlType;
	}

	public void setSqlType(String sqlType) {
		this.sqlType = sqlType;
	}
	/**
	 * Dieser Konstruktor erzeugt die Verbindung zur Datenbank.
	 * @param host - Hostanme
	 * @param port - Portnummer
	 * @param database - Datenbank
	 * @param user - Benutzer
	 * @param password - Passwort
	 * @param sqlType - Datenbanktyp
	 */
	public DBConnection(String host, String port, String database, String user,
			String password, String sqlType) {
		log.info("DBConnection started custom Constructor");
		this.host = host;
		this.port = port;
		this.database = database;
		this.user = user;
		this.password = password;
		this.sqlType = sqlType;

		// delte config and create new one with source property
		ServletContext ctx = (ServletContext) FacesContext.getCurrentInstance()
				.getExternalContext().getContext();
		String rootPath = ctx.getRealPath("/").replace("WebContent/", "");
		File file = new File(rootPath
				+ Configuration.getConfigurationfilename());

		if (file.exists())
			file.delete();
		/*
		 * try { Configuration.addSource(host, port, database, user, password,
		 * SQLServers.MySQL); } catch (ConfigurationException e) {
		 * e.printStackTrace(); }
		 */

	}

	public DBConnection() {
		log.info("DBConnection started");
	}

	// private static ArrayList<ColumnType> columnTypes;
	// private static GenericTableAdapter tableGenerics;
	// private static ArrayList tableContent;
	private List<TableColHeader> columns;
	// private TreeBean t = new TreeBean();
	private Map<Integer, List> a = new HashMap<Integer, List>();
	private List<String> tables;
	/**
	 * Diese Methode stellt die Verbindung zur MYSQL-Datenbank her.
	 * @return null
	 */
	public String connectToMySQLDB() {
		ResultSet rs;
		Connection conn;
		Statement stmt;

		if (sqlType.equals("mysql")) {
			log.info("DBConnection: entering connectToMySQLDB");

			try {

				Class.forName("com.mysql.jdbc.Driver");
				String url = "jdbc:mysql://" + host + ":" + port + "/"
						+ database;
				conn = DriverManager.getConnection(url, user, password);

				String queryString = new String("SHOW TABLES;");

				stmt = conn.createStatement();
				rs = stmt.executeQuery(queryString);
				tables = new ArrayList<String>();

				while (rs.next()) {
					tables.add(new String(rs.getString(1)));

				}
				rs.close();
				stmt.close();
				conn.close();

			} catch (ClassNotFoundException e) {
				e.printStackTrace();
				return null;
			} catch (SQLException e) {
				e.printStackTrace();
				return null;
			}

			log.info("leaving DBConnection:connectToMySQL sucessfully");
			return null;
		} else {
			try {

				String driverName = "oracle.jdbc.driver.OracleDriver";
				Class.forName(driverName);

				String url = String.format("jdbc:oracle:thin:@%s:%s:XE", host, port);
				
				//String url = "jdbc:oracle:thin:@//" + host + ":" + port + "/"
				//		+ database;
				log.info("Oracle Connection String: " + url);
				conn = DriverManager.getConnection(url, user, password);

				DatabaseMetaData md = conn.getMetaData();
				rs = md.getTables(null, user.toUpperCase(), "%", null);

				tables = new ArrayList<String>();

				while (rs.next()) {
					log.info("Oracle Table: " + rs.getString(3));
					tables.add(new String(rs.getString(3)));

				}

				rs.close();
				// stmt.close();
				conn.close();
				return null;
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
				return null;
			} catch (SQLException e) {
				e.printStackTrace();
				return null;
			}

		}

	}
	/**
	 * Diese Methode liefert die Metadaten der Tabelle zurück
	 * @param database - Name der Datenbank
	 * @param table - Name der Tabelle
	 * @return columns - Metadten in einer ArrayList von TableColHeader
	 */
	public List<TableColHeader> getTableMetaData(String database, String table) {
		log.info("dbConnection: entering getTableMetaData");
		ResultSet rs;
		Connection conn;
		Statement stmt;
		if (sqlType.equals("mysql")) {
			log.info("DBConnection: entering connectToMySQLDB");

			try {
				Class.forName("com.mysql.jdbc.Driver");
				String url = "jdbc:mysql://" + host + ":" + port + "/"
						+ database;
				conn = DriverManager.getConnection(url, user,
						password);

				rs = null;
				String queryString = new String("SELECT * FROM "
						+ "INFORMATION_SCHEMA.columns where table_schema='"
						+ database + "'" + "and table_name='" + table + "';");

				stmt = null;
				stmt = conn.createStatement();
				rs = stmt.executeQuery(queryString);

				// tableGenerics = new GenericTableAdapter();
				// columnTypes = new ArrayList<ColumnType>();
				columns = new ArrayList<TableColHeader>();
				while (rs.next()) {
					// System.out.println(rs.getString(4) + " " +
					// rs.getString(8));
					// columnTypes.add(new ColumnType(rs.getString(4),
					// rs.getString(8)));
					// tableGenerics.setColumnType(columnTypes);
					columns.add(new TableColHeader(rs.getString(4), rs
							.getString(8)));
					
					log.info(rs.getString(4) + rs.getString(8));
				}
				log.info("DBConnection: " + columns.size());
				// t.setTableColHeaderList(columns);

				rs.close();
				stmt.close();
				conn.close();

			} catch (SQLException e) {
				e.printStackTrace();
				// return null;
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return columns;
		} else {
			try {

				String driverName = "oracle.jdbc.driver.OracleDriver";
				Class.forName(driverName);

				String url = String.format("jdbc:oracle:thin:@%s:%s:XE", host, port);
				//String url = "jdbc:oracle:thin:@//" + host + ":" + port + "/"
				//		+ database;
				log.info("Oracle Connection String: " + url);
				conn = DriverManager.getConnection(url, user, password);

				DatabaseMetaData myDatabaseMetaData = conn.getMetaData();
		        rs = myDatabaseMetaData.getColumns(null, user.toUpperCase(), table, null);

		        columns = new ArrayList<TableColHeader>();
		       
				while (rs.next()) {
					log.info("Oracle Table: " + rs.getString("COLUMN_NAME") +" "+ rs.getString("DATA_TYPE"));
					columns.add(new TableColHeader(rs.getString("COLUMN_NAME"), rs.getString("DATA_TYPE") ));

				}
				
				rs.close();
				// stmt.close();
				conn.close();
				return columns;
			} 
			catch (ClassNotFoundException e) {
				e.printStackTrace();
				return null;
			} catch (SQLException e) {
				e.printStackTrace();
				return null;
			}
		}
	}
	/**
	 * Diese Methode gibt eine Zeile der Tabelle wieder
	 * @param columnId - ID der Tabelle
	 * @param table - Name der Tabelle
	 * @param columnIndex - Spalten-Index
	 * @return a - Hashmap<Integer, List>
	 */
	public Map<Integer, List> getTableRow(String columnId, String table,
			int columnIndex) {
		a = new HashMap<Integer, List>();
		try {
			Class.forName("com.mysql.jdbc.Driver");
			String url = "jdbc:mysql://" + host + ":" + port + "/" + database;
			Connection conn = DriverManager.getConnection(url, user, password);

			ResultSet rs = null;
			String queryString = new String("SELECT " + columnId + " FROM "
					+ table + ";");

			Statement stmt = null;
			stmt = conn.createStatement();
			rs = stmt.executeQuery(queryString);
			rs.last();
			int numberOfRows = rs.getRow();
			rs.beforeFirst();

			ResultSetMetaData rsMeta = rs.getMetaData();
			int columnNumbers = rsMeta.getColumnCount();
			String[] columnNames = new String[columnNumbers];

			for (int j = 0; j < columnNumbers; j++) {
				columnNames[j] = new String(rsMeta.getColumnName(j + 1));
			}

			List columnData = new ArrayList();

			while (rs.next()) {
				columnData.add(rs.getString(1));

			}

			a.put(columnIndex, columnData);

			rs.close();
			stmt.close();
			conn.close();

			// return "/protected/tableDivide.xhtml";
		} catch (SQLException e) {
			e.printStackTrace();
			// return null;
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return a;

	}
	/**
	 * Diese Methode liefert die Daten der Tabelle in Form einer Liste zurück.
	 * @param database - Name der Datenbank
	 * @param table - Name der Tabelle
	 * @return b - List<Map<Integer, List>>
	 */
	public List<Map<Integer, List>> getTableData(String database, String table) {
		List<Map<Integer, List>> b = new ArrayList<Map<Integer, List>>();
		try {
			Class.forName("com.mysql.jdbc.Driver");
			String url = "jdbc:mysql://" + host + ":" + port + "/" + database;
			Connection conn = DriverManager.getConnection(url, user, password);

			ResultSet rs = null;
			String queryString = new String("SELECT * FROM " + table + ";");

			Statement stmt = null;
			stmt = conn.createStatement();
			rs = stmt.executeQuery(queryString);

			ResultSetMetaData rsMeta = rs.getMetaData();
			int columnNumbers = rsMeta.getColumnCount();
			String[] columnNames = new String[columnNumbers];

			for (int j = 0; j < columnNumbers; j++) {
				columnNames[j] = new String(rsMeta.getColumnName(j + 1));
				getTableRow(columnNames[j], table, j);
			}

			b.add(a);

			// t.setTableContent(b);

			rs.close();
			stmt.close();
			conn.close();

		} catch (SQLException e) {
			e.printStackTrace();

		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return b;
	}
	
	
	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public String getDatabase() {
		return database;
	}

	public void setDatabase(String database) {
		this.database = database;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public List<String> getTables() {
		return tables;
	}

	public void setTables(List<String> tables) {
		this.tables = tables;
	}

}
