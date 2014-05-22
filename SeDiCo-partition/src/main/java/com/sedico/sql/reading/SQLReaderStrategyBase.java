package com.sedico.sql.reading;

import com.sedico.partition.PartitionDescriptor;
import com.sedico.sql.*;

import java.sql.*;
import java.util.*;
/**
 * Diese Klasse implementiert die abstrakte Klasse SQLReaderStrategyBase, welche von der Klasse SQLReaderStrategy erbt.
 * Die Klasse implementiert die Basis der Lesestrategie der SQL-Datenbank.
 * @author jens
 *
 */
public abstract class SQLReaderStrategyBase implements SQLReaderStrategy {
    private final PartitionDescriptor settings;

    public SQLReaderStrategyBase(PartitionDescriptor settings) {
        this.settings = settings;
    }
    /**
     * Diese Methode fügt Zeilen einer Liste hinzu
     * @return new Table - eine neue Tabelle wird zurückgegeben
     */
    @Override
    public Table queryTable(Table table) {
        SQLBuilder builder = createSQLBuilder();
        String select = builder.buildSelect(table, settings.getDatabase());
        List<Row> rows = new ArrayList();
        Row row = execute(select, table);
        rows.add(row);

        return new Table(table.getTableName(), rows, table.getColumnDescriptors());
    }
   
    protected abstract SQLBuilder createSQLBuilder();
    /**
     * Diese Methode fügt jedem Spaltenbeschreiber eine Spalte hinzu.
     * @param statementText - String
     * @param table - Tabelle
     * @return row - Zeile mit den hinzugefügten Spalten
     */
    private Row execute(String statementText, Table table) {
        List<Column> columns = new ArrayList();
        try {
            try(Connection connection = DriverManager.getConnection(settings.getConnectionString(), settings.getUser(), settings.getPassword())) {
                Statement statement = connection.createStatement();
                try(ResultSet resultSet = statement.executeQuery(statementText)) {
                    while(resultSet.next()) {
                        for (ColumnDescriptor descriptor : table.getValueColumnDescriptors()) {
                            Object value = resultSet.getObject(descriptor.getColumnName());
                            //System.out.println("ADDING column: " + descriptor.getColumnName() + " WITH value: " +value);
                            columns.add(new Column(descriptor.getColumnName(), value));
                        }
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        Row row = new Row(columns);
        return row;
    }
}
