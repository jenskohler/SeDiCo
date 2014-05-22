package de.sedico.sql.importing;

import de.sedico.partition.Configuration;
import de.sedico.sql.*;

import java.sql.*;
import java.util.*;
/**
 * Hier wird die Basis einer SQLImportStrategy implementiert. Sie implementiert das Interface SQLImportStrategy.
 * @author jens
 *
 */
public class SQLImportStrategyBase implements SQLImportStrategy {
	private SqlConnectionDescriptor connectionDescriptor = Configuration
			.getSource();
/**
 * Mit dieser Methode kann die Tabelle abgerufen werden. 
 * @return createTable - erzeugt die Tabelle
 */
	@Override
	public Table fetchTable() {
		List<Row> rows = new ArrayList<Row>();
		List<ColumnDescriptor> columns = new ArrayList<ColumnDescriptor>();
		try {
			Connection connection = DriverManager.getConnection(
					connectionDescriptor.getConnectionString(),
					connectionDescriptor.getUser(),
					connectionDescriptor.getPassword());
			columns = getColumns(connection);
			rows = selectData(columns, connection);

		} catch (SQLException e3) {
			System.out.println(e3.getMessage());
		}
		return createTable(rows, columns);
	}
	/**
	 * Diese Methode erzeugt eine Tabelle.
	 * @param rows - Liste von Zeilen
	 * @param columnDescriptors - beschreibt die Spalte
	 * @return new Table - Tabelle
	 */
	private Table createTable(List<Row> rows, List<ColumnDescriptor> columnDescriptors) {
		return new Table(connectionDescriptor.getTable(), rows,
				columnDescriptors);
	}
/**
 * Diese Methode speeichert Reihen mit Spalten ab.
 * @param columnDescriptors - Spaltenbeschreiber
 * @param connection - Verbindung
 * @return tableValues - ArrayList von Zeilen
 * @throws SQLException - falls keine Verbindung hergestellt werden kann
 */
	private List<Row> selectData(List<ColumnDescriptor> columnDescriptorss,	Connection connection) throws SQLException {
		String query = createQueryForColumns(columnDescriptorss);
		List<Row> tableValues = new ArrayList<Row>();
		Statement statement = connection.createStatement();
		ResultSet res = statement.executeQuery(query);
		while (res.next()) {
			List<Column> columns = new ArrayList<Column>();
			for (ColumnDescriptor column : columnDescriptorss) {
				String value = res.getString(column.getColumnName());
				columns.add(new Column(column.getColumnName(), value));
			}
			Row columnValues = new Row(columns);
			tableValues.add(columnValues);
		}

		return tableValues;
	}
	
/**
 * Diese Methode erzeugt eine Abfrage für die Spaltenbeschreiber.
 * @param columns - Liste von Spaltenbeschreibern
 * @return query - String
 */
	private String createQueryForColumns(List<ColumnDescriptor> columns) {
		String columnNames = "";
		for (ColumnDescriptor column : columns) {
			columnNames += column.getColumnName() + ", ";
		}
		columnNames = columnNames.substring(0, columnNames.length() - 2);
		String query = String.format("SELECT %s FROM %s.%s", columnNames,
				connectionDescriptor.getDatabase(),
				connectionDescriptor.getTable());
		return query;
	}
	/**
	 * Diese Methode liefert die Spalten zurück.
	 * @param connection - Verbindung
	 * @return columns - Liste von Spaltenbeschreibern
	 * @throws SQLException - falls keine Verbindung existiert
	 */
	private List<ColumnDescriptor> getColumns(Connection connection) throws SQLException {
		List<String> primaryColumns = getPrimaryKeyColumns(connection);
		List<ColumnDescriptor> columns = new ArrayList<ColumnDescriptor>();
		ResultSet res = connection.getMetaData().getColumns(null,
				connectionDescriptor.getDatabase(),
				connectionDescriptor.getTable(), null);
		while (res.next()) {
			ColumnDescriptor column;
			int columnType = res.getInt("DATA_TYPE");
			long columnSize = res.getLong("COLUMN_SIZE");
			int columnDigits = res.getInt("DECIMAL_DIGITS");

			if (primaryColumns.contains(res.getString("COLUMN_NAME"))) {
				column = new PrimaryColumnDescriptor(
						res.getString("COLUMN_NAME"), columnType, columnSize,
						columnDigits);
			} else {
				column = new ColumnDescriptor(res.getString("COLUMN_NAME"),
						columnType, columnSize, columnDigits);
			}
			columns.add(column);
		}
		return columns;
	}
/**
 * Diese Methode liefert die primären Schlüssel der Spalten zurück.
 * @param connection - Verbindung
 * @return columns - Liste von Strings
 * @throws SQLException - falls keine Verbindung existiert
 */
	private List<String> getPrimaryKeyColumns(Connection connection) throws SQLException {
		List<String> columns = new ArrayList<String>();
		ResultSet res = connection.getMetaData().getPrimaryKeys(null,
				connectionDescriptor.getDatabase(),
				connectionDescriptor.getTable());
		while (res.next()) {
			columns.add(res.getString("COLUMN_NAME"));
		}
		return columns;
	}
}
