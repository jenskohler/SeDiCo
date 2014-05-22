package com.sedico.hibernate;

import com.sedico.partition.TableReader;
import com.sedico.partition.TableWriter;
import com.sedico.sql.*;

import org.hibernate.event.spi.*;
import org.hibernate.persister.entity.AbstractEntityPersister;
import org.hibernate.type.AbstractStandardBasicType;
import org.hibernate.type.Type;

import java.io.Serializable;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
/**
 * Hier wird die Klasse SedicoReadInterceptor implementiert, welche das Interface PreLoadEventListener implementiert.
 * @author jens
 *
 */
public class SedicoReadInterceptor implements PreLoadEventListener {

    private AbstractEntityPersister persister;
/**
 * Diese Methode lädt die Daten um eine Tabelle zu erzeugen. 
 */
    @Override
    public void onPreLoad(PreLoadEvent preLoadEvent) {
        //System.out.println("onPreLoad ausgelöst");

        Serializable id = preLoadEvent.getId();
        Object[] values = preLoadEvent.getState();
        persister = (AbstractEntityPersister)preLoadEvent.getPersister();

        //virtuelle Table aufbauen
        List<Row> rows = new ArrayList();
        List<ColumnDescriptor> columnDescriptors = new ArrayList();
        List<Column> columns = new ArrayList();
        columnDescriptors.add(addIdentifierToColumns(columns, id));
        columnDescriptors.addAll(addPropertiesToColumns(columns, values));
        Row row = new Row(columns);
        rows.add(row);

        //Ergebnisse holen und in values verfrachten
        //System.out.println("Erzeuge virtuelle Tabelle: " + persister.getRootTableName());
        Table table = new Table(persister.getRootTableName(), rows, columnDescriptors);
        TableReader reader = new TableReader();
        table = reader.select(table);
        setValuesFromRow(table.getRows().get(0), values);
    }
/**
 * Diese Methode fügt einer Spalten eine ID hinzu.
 * @param columns - Liste von Spalten
 * @param id - Serializable
 * @return column - Spaltenbeschreiber
 */
    private ColumnDescriptor addIdentifierToColumns(List<Column> columns, Serializable id) {
        for(String name : persister.getIdentifierColumnNames()) {
            //System.out.println("Spalte als PK in virtuelle Tabelle aufgenommen: " + name + " Wert: " + id.toString());
            
           
            columns.add(new Column(name, id.toString()));
  
            //patrick always returns java.type (int code = 2000), but we need to distingish types exactly 
            //ColumnDescriptor descriptor = new PrimaryColumnDescriptor(name, Types.JAVA_OBJECT, 0, 0);
            int mappingCode = getMappingColumnType(name);
            ColumnDescriptor descriptor = new PrimaryColumnDescriptor(name, mappingCode, 0, 0);
            
            
            return descriptor;
        }
        return null;
    }
/**
 * Diese Methode fügt einer Spalte Eigenschaften hinzu.
 * @param columns - Liste von Spalten
 * @param values - Array von Objekten
 * @return columnDescriptors - Collection von ColumnDescriptoren
 */
    private Collection<ColumnDescriptor> addPropertiesToColumns(List<Column> columns, Object[] values) {
        List<ColumnDescriptor> columnDescriptors = new ArrayList();

        for (String propertyName : persister.getPropertyNames()) {
            //System.out.println("Spalte aufgenommen in virtuelle Tabelle: " + propertyName);
            
            int index = persister.getPropertyIndex(propertyName);
            //wenn kein Wert vorhanden bisher, gehen wir davon aus, dass wir den Wert aus der anderen Partition holen müssen
            //if (values[index] == null) {
                String[] columnNames = persister.getPropertyColumnNames(propertyName);
                for (String columnName : columnNames) {
                    columns.add(new Column(columnName, ""));
                    
                    //patrick always returns java.type (int code = 2000), but we need to distingish types exactly 
                    //ColumnDescriptor descriptor = new PrimaryColumnDescriptor(name, Types.JAVA_OBJECT, 0, 0);
                    int mappingCode = getMappingColumnType(columnName);
                    ColumnDescriptor descriptor = new ColumnDescriptor(columnName, mappingCode, 0, 0);
                    //ColumnDescriptor descriptor = new ColumnDescriptor(columnName, Types.JAVA_OBJECT, 0, 0);
                    columnDescriptors.add(descriptor);
                }
            //}
        }
        return columnDescriptors;
    }
/**
 * Diese Methode setzt Werte in der Zeile, falls noch keine vorhanden sind.
 * @param row - Zeile
 * @param values - Array von Objekten
 */
    private void setValuesFromRow(Row row, Object[] values) {
        for (String propertyName : persister.getPropertyNames()) {
            int index = persister.getPropertyIndex(propertyName);
            if (values[index] == null) {
                String[] columnNames = persister.getPropertyColumnNames(propertyName);
                
                
                for (String columnName : columnNames) {
                    Column column = row.getColumn(columnName);

                    Object value = convertFromDatabaseTypeToJavaType(column.getColumnValue(), index);
                    values[index] = value;
                     
                }
            }
        }
    }

    /**
     * es gibt keine Garantie, dass die Datentypen zwischen Datenbanktabelle und Property zusammenpassen
     * über diesen kleinen Trick hier machen wir eine Konvertierung
     */
    private Object convertFromDatabaseTypeToJavaType(Object columnValue, int index) {
        Object value = columnValue;
        if(value != null) {
            AbstractStandardBasicType type = (AbstractStandardBasicType)persister.getPropertyTypes()[index];
            String s = value.toString();
            String t = type.getName().toString();
            //Oracle Hack: Oracle doesn't know, that there is a datatype boolean, so we create our own here
            //After Oracle Hack we need a MySQL hack, because now default behaviour testing if == 1 is deactivated, we 
            //need to test == true as well (thanks Oracle)
            if (t.equals("boolean")) {
            	switch (s) {
            		case "1": 
            		case "true":
            		case "Y": value = new Boolean(true);
            			break;
            		default: 
            			value = new Boolean(false);
            	}
            }
            if (t.equals("timestamp")) {
            	s = s.replace("-", "/");
            }
            else {
            	value = type.fromString(value.toString());
            }
            
    
        }
        return value;
    }
    
    /**
     * Diese Methode fügt Datentypen einen Wert hinzu und liefert ihn zurück.
     * @param columnName - Name der Spalte
     * @return columntype - Wert des Datentyps
     */
    private int getMappingColumnType(String columnName) {
    	Type javaType = persister.getPropertyType(columnName);
    	//System.out.println("SETTING PROPERTY VALUE FOR COLUMN: " +columnName);
    	//set columnType to default value 2000
    	int columnType = 2000;
    	
    	
    	switch (javaType.getName()) {
     		case "integer": columnType = 4;
     						break;
     		case "string": 	columnType = 12;
     						break;
     		case "bit":		columnType = -7;
     						break;
     		case "boolean": columnType = 16;
     						break;
     		case "date":	columnType = 91;
     						break;
     		default: 		columnType = 2000;
     						break;
     }
    	
    	return columnType;
    }
}
