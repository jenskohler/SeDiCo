package de.sedico.sql.reading;

import de.sedico.partition.PartitionDescriptor;
import de.sedico.sql.*;

import java.sql.*;
import java.util.*;
/**
 * Diese abstrakte Klasse implementiert die Basis der SQL-Lesestrategie. Sie implementiert das Interface SQLReaderStrategy.
 * @author jens
 *
 */
public abstract class SQLReaderStrategyBase implements SQLReaderStrategy {
	private final PartitionDescriptor settings;

	public SQLReaderStrategyBase(PartitionDescriptor settings) {
		this.settings = settings;
	}
/**
 * Diese Methode fragt die Tabelle ab.
 * @return new Table - die Tabelle mit den Inhalten
 */
	@Override
	public Table queryTable(Table table) {
		SQLBuilder builder = createSQLBuilder();
		String select = builder.buildSelect(table, settings.getDatabase());
		List<Row> rows = new ArrayList<Row>();
		Row row = execute(select, table);
		rows.add(row);

		return new Table(table.getTableName(), rows,
				table.getColumnDescriptors());
	}

	protected abstract SQLBuilder createSQLBuilder();
/**
 * Diese Methode fügt einer Reihe eine Spalte hinzu.
 * @param statementText - String
 * @param table - Tabelle
 * @return row - Zeile
 */
	private Row execute(String statementText, Table table) {
		List<Column> columns = new ArrayList<Column>();
		try {
			Connection connection = DriverManager.getConnection(
					settings.getConnectionString(), settings.getUser(),
					settings.getPassword());
			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery(statementText);
			while (resultSet.next()) {
				for (ColumnDescriptor descriptor : table
						.getValueColumnDescriptors()) {
					Object value = resultSet.getObject(descriptor
							.getColumnName());
					columns.add(new Column(descriptor.getColumnName(), value));
				}
			}

		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		Row row = new Row(columns);
		return row;
	}
}
