package com.sedico.sql.importing;

import com.sedico.Configuration;
import com.sedico.sql.*;

import java.sql.*;
import java.util.*;
/**
 * Hier wird die Klasse SQLImportStrategyBase implementiert, welche das Interface SQLImportStrategy. Diese Klasse implementiert die Basis der Importstrategie der SQL-Datenbank.
 * @author jens
 *
 */
public class SQLImportStrategyBase implements SQLImportStrategy {
    private SqlConnectionDescriptor connectionDescriptor = Configuration.getSource();
    /**
     * Diese Methode besorgt die Informationen und liefert eine Tabelle zurück.
     * createTable - Tabelle mit Informationen
     */
    @Override
    public Table fetchTable() {
        List<Row> rows = new ArrayList();
        List<ColumnDescriptor> columns = new ArrayList();
        try {
            try(Connection connection = DriverManager.getConnection(connectionDescriptor.getConnectionString(), connectionDescriptor.getUser(), connectionDescriptor.getPassword())) {
                columns = getColumns(connection);
                rows = selectData(columns, connection);
            }
        } catch (SQLException e3) {
            System.out.println(e3.getMessage());
        }
        return createTable(rows, columns);
    }
    /**
     * Die Methode erzeugt eine Tabelle. 
     * @param rows - Liste von Rows
     * @param columnDescriptors - Liste von Spaltenbeschreibern
     * @return new Table - eine neue Tabelle wird zurückgegeben
     */
    private Table createTable(List<Row> rows, List<ColumnDescriptor> columnDescriptors) {
        return new Table(connectionDescriptor.getTable(), rows, columnDescriptors);
    }
    /**
     * Diese Methode fügt Inhalte in die Spalten
     * @param columnDescriptors - Liste von ColumnDescriptors 
     * @param connection - Connection
     * @return tableValues - Liste von Zeilen
     * @throws SQLException - Exception wird gerufen, falls keine Verbindung aufgebaut wird
     */
    private List<Row> selectData(List<ColumnDescriptor> columnDescriptorss, Connection connection) throws SQLException {
        String query = createQueryForColumns(columnDescriptorss);
        List<Row> tableValues = new ArrayList();
        Statement statement = connection.createStatement();
        try(ResultSet res = statement.executeQuery(query)) {
            while (res.next()) {
                List<Column> columns = new ArrayList();
                for(ColumnDescriptor column : columnDescriptorss) {
                    String value = res.getString(column.getColumnName());
                    columns.add(new Column(column.getColumnName(), value));
                }
                Row columnValues = new Row(columns);
                tableValues.add(columnValues);
            }
        }
        return tableValues;
    }
    /**
     * Diese Methode erzeugt die Abfrage der Spalten.
     * @param columns - Lise von ColumnDescriptors
     * @return query - String welcher den Splatennamen, Datenbank und Tabelle aus
     */
    private String createQueryForColumns(List<ColumnDescriptor> columns) {
        String columnNames = "";
        for(ColumnDescriptor column : columns) {
            columnNames += column.getColumnName() + ", ";
        }
        columnNames = columnNames.substring(0, columnNames.length() - 2);
        String query = String.format("SELECT %s FROM %s.%s", columnNames, connectionDescriptor.getDatabase(), connectionDescriptor.getTable());
        return query;
    }
    /**
     * Diese Methode gibt die Spalten zurück. Falls die Liste der primären Splatenbeschreibern einen bestimmten String enthält wird ein primärer Spaltenbeschreiber erzeugt.
     * @param connection - Verbindung
     * @return columns - Eine ArrayList von Spaltenbeschreiber wird zurückgegeben
     * @throws SQLException - Exception wird geworfen, falls keine Verbindung hergestellt werden kann
     */
    private List<ColumnDescriptor> getColumns(Connection connection) throws SQLException {
        List<String> primaryColumns = getPrimaryKeyColumns(connection);
        List<ColumnDescriptor> columns = new ArrayList();
        try(ResultSet res = connection.getMetaData().getColumns(null, connectionDescriptor.getDatabase(), connectionDescriptor.getTable(), null)) {
            while (res.next()) {
                ColumnDescriptor column;
                int columnType = res.getInt("DATA_TYPE");
                long columnSize = res.getLong("COLUMN_SIZE");
                int columnDigits = res.getInt("DECIMAL_DIGITS");

                if (primaryColumns.contains(res.getString("COLUMN_NAME"))) {
                    column = new PrimaryColumnDescriptor(res.getString("COLUMN_NAME"), columnType, columnSize, columnDigits);
                }
                else {
                    column = new ColumnDescriptor(res.getString("COLUMN_NAME"), columnType, columnSize, columnDigits);
                }
                columns.add(column);
            }
        }
        return columns;
    }
    /**
     * Diese Methode gibt die primären Schlüssel der Spalten zurück
     * @param connection - Verbindung
     * @return columns - Liste der Spaltennamen wird zurückgegeben
     * @throws SQLException - Exception wird geworfen, falls keine Verbindung aufgebaut werden kann
     */
    private List<String> getPrimaryKeyColumns(Connection connection) throws SQLException {
        List<String> columns = new ArrayList();
        try(ResultSet res = connection.getMetaData().getPrimaryKeys(null, connectionDescriptor.getDatabase(), connectionDescriptor.getTable())) {
            while (res.next()) {
                columns.add(res.getString("COLUMN_NAME"));
            }
        }
        return columns;
    }
}
