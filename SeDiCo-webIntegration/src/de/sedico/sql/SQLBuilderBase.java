package de.sedico.sql;

import org.hibernate.dialect.Dialect;

import de.sedico.sql.Column;
import de.sedico.sql.ColumnDescriptor;
import de.sedico.sql.Row;
import de.sedico.sql.Table;

import java.util.*;
/**
 * Diese abstrakte Klasse implementiert die Basis eines Erzeugers einer SQL-Datenbank.
 * @author jens
 *
 */
public abstract class SQLBuilderBase implements SQLBuilder {
    /**
     * Diese Methode erzeugt eine Tabelle.
     * @param table - Tabelle
     * @param databaseName - Name der Datenbank
     * @return sql.toString() - String 
     */
	@Override
	public String buildCreateTable(Table table, String databaseName) {
		StringBuilder sql = new StringBuilder();
		sql.append(String.format("CREATE TABLE %s.%s (", databaseName,
				table.getTableName()));
		for (ColumnDescriptor column : table.getPrimaryColumnDescriptors()) {
			sql.append(column.getColumnName());
			sql.append(" ");
			
			String dataType = column.getDataTypeForDialect(getDialect());

			if (dataType.equals("varchar(0)")) {
				sql.append("varchar(255)");
			} else if (dataType.equals("decimal(0,-127)")) {
				sql.append("int");
			} else if (dataType.equals("decimal(0,0)")) {
				sql.append("int");
			} else {
				sql.append(column.getDataTypeForDialect(getDialect()));
			}

			sql.append(" NOT NULL,");
		}
		for (ColumnDescriptor column : table.getValueColumnDescriptors()) {
			sql.append(column.getColumnName());
			sql.append(" ");

			String dataType = column.getDataTypeForDialect(getDialect());

			if (dataType.equals("varchar(0)")) {
				sql.append("varchar(255)");
			} else if (dataType.equals("decimal(0,-127)")) {
				sql.append("int");
			} else if (dataType.equals("decimal(0,0)")) {
				sql.append("int");
			} else if (dataType.equals("varchar2(0 char)")) {
				sql.append("varchar(255)");
			} else {
				sql.append(column.getDataTypeForDialect(getDialect()));
			}

			
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
	 * Diese Methode fügt Inhalte in eine Tabelle ein und liefert die Inhalte zurück.
	 * @param table - Tabelle
	 * @param databaseName - Name der Datenbank
	 * @return inserts - Liste von Inhalten
	 */
	public List<String> buildInserts(Table table, String databaseName) {
        List<String> inserts = new ArrayList();
        StringBuilder sql = new StringBuilder();
        
        for (Row row : table.getRows()) {
            sql.append(String.format("INSERT INTO %s.%s (", databaseName, table.getTableName()));
            for (Column column : row.getColumns()) {
                sql.append(column.getColumnName());
                sql.append(",");
            }
            sql.deleteCharAt(sql.length() - 1);
            sql.append(") VALUES (");
            for (Column column : row.getColumns()) {

                //if column is from datatype bit, do not append ' ' 
            	ColumnDescriptor columnDataType = table.getColumnDescriptor(column.getColumnName());
            	String dataTypeColumn = columnDataType.getDataTypeForDialect(getDialect());
            	
            	
            	if (column.getColumnValue() != null) {
            		if (dataTypeColumn.equals("bit")) {
                		sql.append(column.getColumnValue());
                	}
            		else if (dataTypeColumn.equals("date")) {
            			sql.append("'" + column.getColumnValue() + "'");
            		}
            		else if (dataTypeColumn.equals("timestamp")) {
            			sql.append("'" + column.getColumnValue() + "'");
            		}
            		else if (dataTypeColumn.equals("varchar")) {
            			sql.append("'" + column.getColumnValue() + "'");
            		}
					else if (dataTypeColumn.equals("boolean")) {
						if (column.getColumnValue().equals("true")) {sql.append("1"); } else { sql.append("0"); } 
					}
					else if (dataTypeColumn.equals("char")) {
						sql.append("'" + column.getColumnValue() + "'");
					}
					else if (dataTypeColumn.equals("decimal")) {
						sql.append(column.getColumnValue());
					}
					
					else if (dataTypeColumn.equals("integer")) {
						sql.append(column.getColumnValue());
					}
	
					else {
						sql.append("'" + column.getColumnValue() + "'");
					}
            	}
            	else {
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
	 * Diese Methode löscht Inhalte einer Tabelle.
	 * @param table - Tabelle
	 * @param databaseName - Name der Datenbank
	 * @return deletes - die gelöschten Inhalte 
	 */
	@Override
	public List<String> buildDeletes(Table table, String databaseName) {
		List<String> deletes = new ArrayList<String>();
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
	 * Diese Methode erstellt einen String mit Datenbank- und Tabellennamen.
	 * @param table - Tabelle 
	 * @param databaseName - Name der Datenbank
	 * @return drop - String 
	 */
	@Override
	public List<String> buildDrops(Table table, String databaseName) {
		List<String> drop = new ArrayList<String>();
		StringBuilder sql = new StringBuilder();
		sql.append(String.format("DROP TABLE %s.%s", databaseName,
				table.getTableName()));
		drop.add(sql.toString());
		sql = new StringBuilder();

		return drop;
	}
	/**
	 * Diese Methode aktualisiert die Inhalte der Tabelle.
	 * @param table - Tabelle
	 * @param databaseName - Name der Datenbank
	 * @return updates - die aktualisierten Inhalte
	 */
	@Override
	public List<String> buildUpdates(Table table, String databaseName) {
		List<String> updates = new ArrayList<String>();
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
					
					sql.append(columnName);
					sql.append(" = ");
					
					ColumnDescriptor columnDataType = table.getColumnDescriptor(column.getColumnName());
	            	String dataTypeColumn = columnDataType.getDataTypeForDialect(getDialect());
					
	            	if (column.getColumnValue() != null) {
	            		if (dataTypeColumn.equals("bit")) {
	                		sql.append(column.getColumnValue());
	                	}
	            		else if (dataTypeColumn.equals("date")) {
	            			sql.append("'" + column.getColumnValue() + "'");
	            		}
	            		else if (dataTypeColumn.equals("timestamp")) {
	            			sql.append("'" + column.getColumnValue() + "'");
	            		}
	            		else if (dataTypeColumn.equals("varchar")) {
	            			sql.append("'" + column.getColumnValue() + "'");
	            		}
						else if (dataTypeColumn.equals("boolean")) {
							if (column.getColumnValue().equals("true")) {sql.append("1"); } else { sql.append("0"); } 
						}
						else if (dataTypeColumn.equals("char")) {
							sql.append("'" + column.getColumnValue() + "'");
						}
						else if (dataTypeColumn.equals("decimal")) {
							sql.append(column.getColumnValue());
						}
						
						else if (dataTypeColumn.equals("integer")) {
							sql.append(column.getColumnValue());
						}
		
						else {
							sql.append("null");
						}
	            	}
					
					
					sql.append(",");
				}
			}
			sql.deleteCharAt(sql.length() - 1);
			addWhereClauseForPrimaryKeys(sql, table, row);
			updates.add(sql.toString());
			sql = new StringBuilder();
		}
		return updates;
	}
/**
 * Diese Methode erzeugt eine Auswahl von Strings.
 * @param table - Tabelle
 * @param databaseName - Name der Datenbank 
 * @return sql.toString() - die Auswahl der String
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
 * Diese Methode fügt an primäre Spaltenbeschreibern Inhalte an.
 * @param sql - StringBuilder
 * @param table - Tabelle
 * @param row - Zeile 
 */
	private void addWhereClauseForPrimaryKeys(StringBuilder sql, Table table,
			Row row) {
		sql.append(" WHERE ");
		for (ColumnDescriptor descriptor : table.getPrimaryColumnDescriptors()) {
			String columnName = descriptor.getColumnName();
			Column column = row.getColumn(columnName);
			sql.append(columnName);
			sql.append(" = ");
			sql.append("'" + column.getColumnValue() + "'");
			sql.append(" AND ");
		}
		sql.delete(sql.length() - 5, sql.length());
	}

	protected abstract Dialect getDialect();
}
