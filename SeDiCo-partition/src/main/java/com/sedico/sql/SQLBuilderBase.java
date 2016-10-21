package com.sedico.sql;

import org.hibernate.dialect.Dialect;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Dises Klasse lässt Tabellen erzeugen, Inhalte in Tabellen einfügen und
 * Tabellen aktualisieren. Die Klasse implementiert das Interface SQLBuilder.
 * 
 * @author jens
 *
 */
public abstract class SQLBuilderBase implements SQLBuilder {
	/**
	 * Diese Methode erzeugt eine Tabelle
	 * 
	 * @param table
	 *            - Tabelle, die erzeugt werden soll
	 * @param databaseName
	 *            - Name der Datenbank
	 * @param isPrimaryPartition
	 *            - Boolean ob Primary oder nicht Primary Partition
	 * @return sql.toString() - Informationen der Tabelle
	 */
	@Override
	public String buildCreateTable(Table table, String databaseName,
			boolean isPrimaryPartition) {
		StringBuilder sql = new StringBuilder();
		sql.append(String.format("CREATE TABLE %s.%s (", databaseName,
				table.getTableName()));
		// sql.append("CREATE TABLE " + databaseName + "." +
		// table.getTableName() + "(");
		for (ColumnDescriptor column : table.getPrimaryColumnDescriptors()) {
			sql.append(column.getColumnName());
			sql.append(" ");
			// varchar(0) HACK: if varchar(0) is returned replace it with
			String dataType = column.getDataTypeForDialect(getDialect());
			switch (dataType) {
			case "varchar(0)":
				sql.append("varchar(255)");
				break;
			case "decimal(0,-127)":
			case "decimal(0,0)":
				sql.append("int");
				break;

			default:
				sql.append(column.getDataTypeForDialect(getDialect()));
			}

			/*
			 * if (dataType.equals("varchar(0)")) { dataType = "varchar(255)";
			 * sql.append(dataType); } else {
			 * sql.append(column.getDataTypeForDialect(getDialect())); }
			 */
			sql.append(" NOT NULL,");
		}
		for (ColumnDescriptor column : table.getValueColumnDescriptors()) {
			sql.append(column.getColumnName());
			sql.append(" ");
			String dataType = column.getDataTypeForDialect(getDialect());
			// varchar(0) HACK: if varchar(0) is returned replace it with
			switch (dataType) {

			case "varchar(0)":
				dataType = "varchar(255)";
				sql.append(dataType);
				break;
			case "varchar2(0 char)":
				dataType = "varchar2(255)";
				sql.append(dataType);
				break;
			case "decimal(0,-127)":
			case "decimal(0,0)":
				dataType = "int";
				sql.append(dataType);
				break;
			default:
				sql.append(column.getDataTypeForDialect(getDialect()));
			}

			sql.append(",");

		}
		ColumnDescriptor lastUpdateColumnDescriptor = new ColumnDescriptor(
				"LAST_UPDATE", 93, 0, 0);
		ColumnDescriptor isActiveColumnDescriptor = new ColumnDescriptor(
				"IS_ACTIVE", 16, 0, 0);
		if (isPrimaryPartition) {
			sql.append(lastUpdateColumnDescriptor.getColumnName());
			sql.append(" ");
			sql.append(lastUpdateColumnDescriptor
					.getDataTypeForDialect(getDialect()));
			sql.append(" DEFAULT CURRENT_TIMESTAMP");
			sql.append(" NOT NULL,");
			sql.append(isActiveColumnDescriptor.getColumnName());
			sql.append(" ");
			sql.append(isActiveColumnDescriptor
					.getDataTypeForDialect(getDialect()));
			sql.append(" DEFAULT 1");
			sql.append(" NOT NULL,");
		} else {
			sql.append(lastUpdateColumnDescriptor.getColumnName());
			sql.append(" ");
			sql.append(lastUpdateColumnDescriptor
					.getDataTypeForDialect(getDialect()));
			sql.append(",");
			sql.append(isActiveColumnDescriptor.getColumnName());
			sql.append(" ");
			sql.append(isActiveColumnDescriptor
					.getDataTypeForDialect(getDialect()));
			sql.append(",");
		}
		sql.append("PRIMARY KEY(");
		for (ColumnDescriptor column : table.getPrimaryColumnDescriptors()) {
			sql.append(column.getColumnName());
			sql.append(",");
		}
		sql.deleteCharAt(sql.length() - 1);
		sql.append("))");
		return sql.toString();
	}

	/**
	 * Die Methode fügt Inhalte in eine übergebene Tabelle ein
	 * 
	 * @param table
	 *            - Tabelle, in die Inhalte eingefügt werden
	 * @param databaseName
	 *            - Name der Datenbank
	 * @return inserts - Die eingefügten Inhalte werden zurückgegeben
	 */
	@Override
	public List<String> buildInserts(Table table, String databaseName) {
		List<String> inserts = new ArrayList();
		StringBuilder sql = new StringBuilder();

		for (Row row : table.getRows()) {

			sql.append(String.format("INSERT INTO %s.%s (", databaseName,
					table.getTableName()));
			// sql.append("INSERT INTO '"+databaseName+"."+table.getTableName()+"' (");

			for (Column column : row.getColumns()) {
				sql.append(column.getColumnName());
				sql.append(",");
			}
			sql.deleteCharAt(sql.length() - 1);
			sql.append(") VALUES (");
			for (Column column : row.getColumns()) {

				// if column is from datatype bit, do not append ' '
				ColumnDescriptor columnDataType = table
						.getColumnDescriptor(column.getColumnName());
				int dataTypeColumn = columnDataType.getNativeColumnType();
				String s = String.valueOf(dataTypeColumn);
				/*
				 * if (dataTypeColumn.equals("BIT")) {
				 * sql.append(column.getColumnValue()); } else { sql.append("'"
				 * + column.getColumnValue() + "'"); }
				 */
				CharSequence seq1 = ":";
				CharSequence seq2 = "/";
				SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
				if (column.getColumnValue() != null) {
					switch (dataTypeColumn) {
					case 91:
						if (column.getColumnValue() instanceof Date) {
							sql.append("'" + df.format(column.getColumnValue())
									+ "'");
						} else {
							sql.append("'" + column.getColumnValue() + "'");
						}
						break;
					case 93:
					case 12:
						sql.append("'" + column.getColumnValue() + "'");
						break;
					case 16:
						if (column.getColumnValue().equals("true")) {
							sql.append("1");
						} else {
							sql.append("0");
						}
						break;
					case 1:
						if (column.getColumnValue().equals("Y")) {
							sql.append("1");
						} else {
							sql.append("0");
						}
						break;
					case 8:
					case -7:
					case 3:
					case 4:
						sql.append(column.getColumnValue());
						break;
					case 2000:
						if (column.getColumnValue() != null) {
							if (column.getColumnValue().toString()
									.contains(seq1)
									|| column.getColumnValue().toString()
											.contains(seq2)) {
								sql.append("'" + column.getColumnValue() + "'");
							} else {
								sql.append(column.getColumnValue());
							}
						} else {
							sql.append("null");
						}

						break;

					default:
						break;
					}

				} else {
					sql.append("null");
				}

				sql.append(",");
			}
			sql.deleteCharAt(sql.length() - 1);
			sql.append(")");
			inserts.add(sql.toString());
			sql = new StringBuilder();
		}
		return inserts;
	}

	/**
	 * Die Methode löscht Reihe für Reihe die Inhalte der Tabelle
	 * 
	 * @param table
	 *            - Tabelle, in der Inhalte gelöscht werden
	 * @param databaseName
	 *            - Name der Datenbank
	 * @return deletes - Liste von Strings der gelöschten Inhalte
	 */
	@Override
	public List<String> buildDeletes(Table table, String databaseName) {
		List<String> deletes = new ArrayList();
		StringBuilder sql = new StringBuilder();
		for (Row row : table.getRows()) {
			sql.append(String.format("DELETE FROM %s.%s", databaseName,
					table.getTableName()));
			addWhereClauseForPrimaryKeys(sql, table, row);
			deletes.add(sql.toString());
			sql = new StringBuilder();
		}
		return deletes;
	}

	/**
	 * Die Methode aktualisiert eine Tabelle.
	 * 
	 * @param table
	 *            - Tabelle, welche aktualisiert werden soll
	 * @param databaseName
	 *            - Name der Datenbank
	 * @return updates - Liste von Strings der aktualisierten Inhalte
	 */
	@Override
	public List<String> buildUpdates(Table table, String databaseName) {
		List<String> updates = new ArrayList();
		StringBuilder sql = new StringBuilder();
		for (Row row : table.getRows()) {
			sql.append(String.format("UPDATE %s.%s SET ", databaseName,
					table.getTableName()));
			for (ColumnDescriptor descriptor : table
					.getValueColumnDescriptors()) {
				String columnName = descriptor.getColumnName();
				Column column = row.getColumn(columnName);

				// Eine Spalte kann einen Descriptor aber keine Daten haben,
				// wenn die Daten in der anderen Partition sind.
				if (column != null) {
					int dataTypeColumn = descriptor.getNativeColumnType();
					String s = String.valueOf(dataTypeColumn);
					/*
					 * if (dataTypeColumn.equals("BIT")) {
					 * sql.append(column.getColumnValue()); } else {
					 * sql.append("'" + column.getColumnValue() + "'"); }
					 */
					sql.append(columnName);
					sql.append(" = ");
					// handle special datatypes like boolean or bit

					CharSequence seq1 = ":";
					CharSequence seq2 = "/";
					SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
					if (column.getColumnValue() != null) {
						switch (dataTypeColumn) {

						case 91:
							sql.append("'" + df.format(column.getColumnValue())
									+ "'");
							break;
						case 93:
						case 12:
							if (column.getColumnValue() != null) {
								sql.append("'" + column.getColumnValue() + "'");
							} else {
								sql.append("''");
							}
							;
							break;
						case 16:
							if (column.getColumnValue().equals("true")) {
								sql.append("1");
							} else {
								sql.append("0");
							}
							break;
						case -7:
						case 3:
						case 4:
							if (column.getColumnValue() != null) {
								sql.append("'" + column.getColumnValue() + "'");
							} else {
								sql.append("''");
							}
							;

							break;
						case 2000:
							if (column.getColumnValue() != null) {
								if (column.getColumnValue().toString()
										.contains(seq1)
										|| column.getColumnValue().toString()
												.contains(seq2)) {
									sql.append("'" + column.getColumnValue()
											+ "'");
								} else {
									sql.append(column.getColumnValue());
								}
							} else {
								sql.append("null");
							}

							break;

						default:
							break;
						}
					} else {
						sql.append("null");
					}
					sql.append(",");
				}

				/*
				 * if (column != null) { sql.append(columnName);
				 * sql.append(" = "); sql.append("'" + column.getColumnValue() +
				 * "'"); sql.append(","); }
				 */
			}
			sql.deleteCharAt(sql.length() - 1);
			addWhereClauseForPrimaryKeys(sql, table, row);
			updates.add(sql.toString());
			sql = new StringBuilder();
		}
		return updates;
	}

	/**
	 * Die Methode gibt die Namen der Tabelle, Spalten und Datenbank als String
	 * zurück
	 * 
	 * @param Tabelle
	 *            - Tabelle, die durchlaufen wird
	 * @param databaseName
	 *            - Name der Datenbank
	 * @return sql.toString() - Die Informationen werden als String
	 *         zurückgegeben
	 */
	@Override
	public String buildSelect(Table table, String databaseName) {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT ");
		for (ColumnDescriptor descriptor : table.getColumnDescriptors()) {
			sql.append(descriptor.getColumnName());
			sql.append(",");
		}
		sql.deleteCharAt(sql.length() - 1);
		sql.append(" FROM ");
		sql.append(String.format("%s.%s", databaseName, table.getTableName()));
		addWhereClauseForPrimaryKeys(sql, table, table.getRows().get(0));
		return sql.toString();
	}

	/**
	 * Die Methode bildet einen String von allen Spalten und ihren Werten mit
	 * jedem Spaltenbeschreiber.
	 * 
	 * @param sql
	 *            - StringBuilder
	 * @param table
	 *            - Tabelle
	 * @param row
	 *            - Zeile in der Tabelle
	 */
	private void addWhereClauseForPrimaryKeys(StringBuilder sql, Table table,
			Row row) {
		sql.append(" WHERE ");
		for (ColumnDescriptor descriptor : table.getPrimaryColumnDescriptors()) {
			String columnName = descriptor.getColumnName();
			Column column = row.getColumn(columnName);
			if (column == null) {
				throw new NoSuchElementException(
						"Es wurde eine Spalte "
								+ columnName
								+ " angefordert, die nicht existiert oder nicht als PK definiert ist.");
			}
			sql.append(columnName);
			sql.append(" = ");
			sql.append("'" + column.getColumnValue() + "'");
			sql.append(" AND ");
		}
		sql.delete(sql.length() - 5, sql.length());
	}

	/**
	 * Abstrakte Methode
	 * 
	 * @return Dialect
	 */
	protected abstract Dialect getDialect();
}
