package com.sedico.sql.writing;

import com.sedico.partition.*;
import com.sedico.sql.*;

import java.sql.*;
import java.util.*;
/**
 * Hier wird die abstrakte Klasse SQLWriterStrategyBase implementiert, welche von der Klasse SQLWriterStrategy erbt.
 * Die Klasse implementiert die Basis der Schreibstrategie einer SQL-Datenbank.
 * @author jens
 *
 */
public abstract class SQLWriterStrategyBase implements SQLWriterStrategy  {

    private final PartitionDescriptor settings;

    protected SQLWriterStrategyBase(PartitionDescriptor settings) {
        this.settings = settings;
    }
    /**
     * Diese Methode erzeugt eine Tabelle.
     */
    @Override
    public void createTable(Table table) {
        Table newTable = new Table(settings.getTable(), table.getRows(), table.getColumnDescriptors());
        SQLBuilder builder = createSQLBuilder();
        String create = builder.buildCreateTable(newTable, settings.getDatabase(),settings.isPrimary());
        List<String> creates = new ArrayList();
        creates.add(create);
        execute(creates);
    }
    /**
     * Diese Methode fügt Inhalte in eine Tabelle ein.
     */
    @Override
    public void insertTable(Table table) {
        Table newTable = new Table(settings.getTable(), table.getRows(), table.getColumnDescriptors());
        
        SQLBuilder builder = createSQLBuilder();
        List<String> inserts = builder.buildInserts(newTable, settings.getDatabase());
        execute(inserts);
    }
    /**
     * Diese Methode löscht den Inhalt einer Tabelle.
     */
    @Override
    public void deleteTable(Table table) {
        Table newTable = new Table(settings.getTable(), table.getRows(), table.getColumnDescriptors());
        SQLBuilder builder = createSQLBuilder();
        List<String> deletes = builder.buildDeletes(newTable, settings.getDatabase());
        execute(deletes);
    }
    /**
     * Diese Methode aktualisiert eine Tabelle.
     */
    @Override
    public void updateTable(Table table) {
        Table newTable = new Table(settings.getTable(), table.getRows(), table.getColumnDescriptors());
        SQLBuilder builder = createSQLBuilder();
        List<String> updates = builder.buildUpdates(newTable, settings.getDatabase());
        execute(updates);
    }
    /**
     * Diese Methode fügt textStatements einer Liste hinzu und führt diese aus.
     * @param statementTexts - Liste von Strings
     */
    private void execute(List<String> statementTexts) {
        
    	final Statement statement;
    	try {
            try(Connection connection = DriverManager.getConnection(settings.getConnectionString(), settings.getUser(), settings.getPassword())) {
                statement = connection.createStatement();
                
                
                /*
                for (String statementText : statementTexts) {
                    statement.addBatch(statementText);
                }
                */
                
                Parallel.For(statementTexts, 
                		 // The operation to perform with each item
                		 new Parallel.Operation<String>() {
                		    public void perform(String statementText) {
                		    	try {
									statement.addBatch(statementText);
								} catch (SQLException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
                		    };
                		});
                
                statement.executeBatch();
                connection.close();
            }
        } catch (SQLException e3) {
            System.out.println(e3.getMessage());
        }
    }

    protected abstract SQLBuilder createSQLBuilder();
}
