package com.sedico.hibernate;

import com.sedico.partition.*;
import com.sedico.sql.*;

import org.hibernate.event.spi.*;
import org.hibernate.persister.entity.*;
import org.hibernate.tuple.IdentifierProperty;
import org.hibernate.tuple.StandardProperty;
import org.hibernate.type.AbstractStandardBasicType;
import org.hibernate.type.Type;

import java.io.Serializable;
import java.sql.Types;
import java.util.*;
/**
 * Hier wird die Klasse SedicoWriteInterceptor, welche die Interfaces PreInsertEventListener, PreUpdateEventListener, PreDeleteEventListener implementiert.
 * @author jens
 *
 */
public class SedicoWriteInterceptor implements PreInsertEventListener, PreUpdateEventListener, PreDeleteEventListener {
/**
 * Diese Methode erzegut einen Tabellenschreiber und erzeugt damit eine Tabelle.
 * @param preInsertEvent - PreInsertEvent
 * @return true
 */
    @Override
    public boolean onPreInsert(PreInsertEvent preInsertEvent) {
        AbstractEntityPersister persister = (AbstractEntityPersister)preInsertEvent.getPersister();
        Table table = createTableFromEntity(persister, preInsertEvent.getEntity());
        TableWriter writer = new TableWriter();
        writer.insert(table);
        return true; //veto insert
    }
    /**
     * Diese Methode erzegut einen Tabellenschreiber und aktualisiert mit ihm eine Tabelle.
     * @param preUpdateEvent - PreUpdateEvent
     * @return true
     */
    @Override
    public boolean onPreUpdate(PreUpdateEvent preUpdateEvent) {
        AbstractEntityPersister persister = (AbstractEntityPersister)preUpdateEvent.getPersister();
        Table table = createTableFromEntity(persister, preUpdateEvent.getEntity());
        TableWriter writer = new TableWriter();
        writer.update(table);
        return true; //veto update
    } 
    /**
     * Diese Methode erzegut einen Tabellenschreiber und löscht mit ihm eine Tabelle.
     * @param preDeleteEvent - PreDeleteEvent
     * @return true
     */
    @Override
    public boolean onPreDelete(PreDeleteEvent preDeleteEvent) {
        AbstractEntityPersister persister = (AbstractEntityPersister)preDeleteEvent.getPersister();
        Table table = createTableFromEntity(persister, preDeleteEvent.getEntity());
        TableWriter writer = new TableWriter();
        writer.delete(table);
        return true; //veto delete
    }
/**
 * Diese Methode erzeugt eine Tabelle anhand einer Entity.
 * @param persister - AbstractEntityPersister
 * @param entity - Object
 * @return table - Tabelle mit der Entity
 */
    private Table createTableFromEntity(AbstractEntityPersister persister, Object entity) {
        List<Row> rows = new ArrayList();
        List<ColumnDescriptor> columnDescriptors = new ArrayList();
        List<Column> columns = new ArrayList();
        columnDescriptors.add(getPrimaryKeyColumn(persister, entity, columns));

        //Datenspalten hinzufügen
        for (String name : persister.getPropertyNames()) {
            Object propertyValue = persister.getPropertyValue(entity, name);
            String[] propertyColumnNames = persister.getPropertyColumnNames(name);
            for (String propertyColumnName : propertyColumnNames) {
            	int mappingCode = getMappingColumnType(persister, name);
            	//patrick always returns java.type (int code = 2000), but we need to distingish types exactly 
                //ColumnDescriptor descriptor = new ColumnDescriptor(propertyColumnName, Types.JAVA_OBJECT, 0, 0);
            	ColumnDescriptor descriptor = new ColumnDescriptor(name, mappingCode, 0, 0);
              
                columnDescriptors.add(descriptor);
                if (propertyValue != null) {
                	columns.add(new Column(propertyColumnName, propertyValue.toString()));
                }
                else {
                	columns.add(new Column(propertyColumnName, null));
                }
            }
        }
        Row row = new Row(columns);
        rows.add(row);
        Table table = new Table(persister.getRootTableName(), rows, columnDescriptors);
        return table;
    }
/**
 * Diese Methode liefert die primären Schlüssel für eine Spalte zurück.
 * @param persister - AbstractEntityPersister
 * @param entity - Object
 * @param columns - Liste von Spalten
 * @return descriptor - Spaltenbeschreiber
 */
    private ColumnDescriptor getPrimaryKeyColumn(AbstractEntityPersister persister, Object entity, List<Column> columns) {
        String name = persister.getIdentifierPropertyName();
        Object propertyValue = persister.getIdentifier(entity);

      //patrick always returns java.type (int code = 2000), but we need to distingish types exactly 
        //ColumnDescriptor descriptor = new PrimaryColumnDescriptor(name, Types.JAVA_OBJECT, 0, 0);
        int mappingCode = getMappingColumnType(persister, name);
        ColumnDescriptor descriptor = new PrimaryColumnDescriptor(name, mappingCode, 0, 0);
        
        //ColumnDescriptor descriptor = new PrimaryColumnDescriptor(name, Types.JAVA_OBJECT, 0, 0);
        columns.add(new Column(name, propertyValue.toString()));
        return descriptor;
    }
/**
 * Diese Methode weist einen Datentyp einen Wert zu und liefert ihn zurück.    
 * @param persister - AbstractEntityPersister
 * @param columnName - Spaltenname
 * @return columnType - Wert des Datentyps
 */
    private int getMappingColumnType(AbstractEntityPersister persister, String columnName) {
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
