package de.sedico.sql.writing;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import de.sedico.partition.PartitionDescriptor;
import de.sedico.sql.SQLBuilder;
import de.sedico.sql.Table;
/**
 * Diese abstrakte Klasse implementiert die Basis der Schreibestrategie der SQL-Datenbank. Sie implementiert das Interface SQLWriterStrategy.
 * @author jens
 *
 */
public abstract class SQLWriterStrategyBase implements SQLWriterStrategy {

	    private final PartitionDescriptor settings;

	    protected SQLWriterStrategyBase(PartitionDescriptor settings) {
	        this.settings = settings;
	    }
	    /**
	     * Diese Methode erzeugt eine Tabelle.
	     * @param table - zu erzeugende Tabelle
	     */
	    @Override
	    public void createTable(Table table) {
	        Table newTable = new Table(settings.getTable(), table.getRows(), table.getColumnDescriptors());
	        SQLBuilder builder = createSQLBuilder();
	        String create = builder.buildCreateTable(newTable, settings.getDatabase());
	        List<String> creates = new ArrayList<String>();
	        creates.add(create);
	        execute(creates);
	    }
	    
/**
 * Diese Methode füllt eine Liste mit der Tabelle und den Einstellungen der Datenbank.
 * @param table - Tabelle
 */
	    @Override
	    public void dropTable(Table table) {
	        Table newTable = new Table(settings.getTable(), table.getRows(), table.getColumnDescriptors());
	        SQLBuilder builder = createSQLBuilder();
	        List<String> drops = builder.buildDrops(newTable, settings.getDatabase());
	        execute(drops);
	    }
	    /**
	     * Diese Methode fügt Inhalte in die Tabelle ein.
	     * @param table - Tabelle, die mit Inhalten gefüllt wird
	     * 
	     */
	    @Override
	    public void insertTable(Table table) {
	        Table newTable = new Table(settings.getTable(), table.getRows(), table.getColumnDescriptors());
	        SQLBuilder builder = createSQLBuilder();
	        List<String> inserts = builder.buildInserts(newTable, settings.getDatabase());
	        execute(inserts);
	    }
/**
 * Diese Methode löscht Inhalte aus der Tabelle.
 * @param table - Tabelle, die gelöscht wird
 */
	    @Override
	    public void deleteTable(Table table) {
	        Table newTable = new Table(settings.getTable(), table.getRows(), table.getColumnDescriptors());
	        SQLBuilder builder = createSQLBuilder();
	        List<String> deletes = builder.buildDeletes(newTable, settings.getDatabase());
	        execute(deletes);
	    }
/**
 * Die Methode aktualisiert Inhalte in einer Tabelle.
 * @param table - Tabelle, die aktualisiert wird
 */
	    @Override
	    public void updateTable(Table table) {
	        Table newTable = new Table(settings.getTable(), table.getRows(), table.getColumnDescriptors());
	        SQLBuilder builder = createSQLBuilder();
	        List<String> updates = builder.buildUpdates(newTable, settings.getDatabase());
	        execute(updates);
	    }
	    
	    /**
	     * Diese Methode fügt schrittweise Statements einer Liste hinzu.
	     * @param statementTexts - Liste von Strings
	     */
	    private void execute(List<String> statementTexts) {
	        try {
	        	Connection connection = DriverManager.getConnection(settings.getConnectionString(), settings.getUser(), settings.getPassword());
	        	Statement statement = connection.createStatement();
                for (String statementText : statementTexts) {
                    statement.addBatch(statementText);
                }
                statement.executeBatch();
	            connection.close();
	        } catch (SQLException e3) {
	            System.out.println(e3.getMessage());
	        }
	    }

	    protected abstract SQLBuilder createSQLBuilder();
	}
