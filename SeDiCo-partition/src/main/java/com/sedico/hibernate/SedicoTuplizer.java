package com.sedico.hibernate;

import com.sedico.partition.TableReader;
import com.sedico.sql.*;

import org.hibernate.HibernateException;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.metamodel.binding.EntityBinding;
import org.hibernate.persister.entity.*;
import org.hibernate.tuple.*;
import org.hibernate.tuple.entity.*;
import org.hibernate.type.*;

import java.io.Serializable;
import java.sql.Types;
import java.util.*;
/**
 * Diese Klasse implementiert die Klasse SedicoTuplizer, welche von der Klasse PojoEntityTuplizer erbt.
 * @author jens
 *
 */
public class SedicoTuplizer extends PojoEntityTuplizer {

    private AbstractEntityPersister persister;

    public SedicoTuplizer(EntityMetamodel entityMetamodel, PersistentClass mappedEntity) {
        super(entityMetamodel, mappedEntity);
    }

    public SedicoTuplizer(EntityMetamodel entityMetamodel, EntityBinding mappedEntity) {
        super(entityMetamodel, mappedEntity);
    }
/**
 * Diese Methode setzt die Werte der Eigenschaften.
 * @param entity - Obejct
 * @param values - Array von Objekten
 * @throws HibernateException - falls keine HibernateConfiguration erzeugt wurde
 */
    @Override
    public void setPropertyValues(Object entity, Object[] values) throws HibernateException {
        persister = (AbstractEntityPersister)this.getFactory().getEntityPersister(getEntityName());

        //virtuelle Table aufbauen
        List<Row> rows = new ArrayList();
        List<ColumnDescriptor> columnDescriptors = new ArrayList();
        List<Column> columns = new ArrayList();
        columnDescriptors.add(addIdentifierToColumns(columns, entity));
        columnDescriptors.addAll(addPropertiesToColumns(columns, values));
        Row row = new Row(columns);
        rows.add(row);

        //Ergebnisse holen und in values verfrachten
        Table table = new Table(persister.getRootTableName(), rows, columnDescriptors);
        TableReader reader = new TableReader();
        table = reader.select(table);
        setValuesFromRow(table.getRows().get(0), values);

        super.setPropertyValues(entity, values);
    }
    /**
     * Diese Methode fügt zu einer Spalte eine Entity hinzu.
     * @param columns - Liste von Spalten
     * @param entity - Objekt zur Identifikation
     * @return descriptor - Spaltenbeschreiber
     */
    private ColumnDescriptor addIdentifierToColumns(List<Column> columns, Object entity) {
        Serializable id = this.getIdentifier(entity);
        for(String name : persister.getIdentifierColumnNames()) {
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
 * Diese Methode weist allen Spalten einen Wert hinzu, falls noch keiner vorhanden ist.
 * @param columns - Liste von Spalten
 * @param values - Array von Objekten
 * @return columnDescriptors - Collection von Spaltenbeschreibern
 */
    private Collection<ColumnDescriptor> addPropertiesToColumns(List<Column> columns, Object[] values) {
        List<ColumnDescriptor> columnDescriptors = new ArrayList();

        for (String propertyName : persister.getPropertyNames()) {
            int index = persister.getPropertyIndex(propertyName);
            //wenn kein Wert vorhanden bisher, gehen wir davon aus, dass wir den Wert aus der anderen Partition holen müssen
            if (values[index] == null) {
                String[] columnNames = persister.getPropertyColumnNames(propertyName);
                for (String columnName : columnNames) {
                    columns.add(new Column(columnName, ""));
                    
                  //patrick always returns java.type (int code = 2000), but we need to distingish types exactly 
                    //ColumnDescriptor descriptor = new ColumnDescriptor(columnName, Types.JAVA_OBJECT, 0, 0);
                    int mappingCode = getMappingColumnType(columnName);
                    ColumnDescriptor descriptor = new PrimaryColumnDescriptor(columnName, mappingCode, 0, 0);
                    columnDescriptors.add(descriptor);
                }
            }
        }
        return columnDescriptors;
    }
/**
 * Diese Methode setzt die Werte in einer Zeile.
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
                    	//System.out.println("Setting up virtual table column: " +column);
                    	if (column != null) {
                    		Object value = convertFromDatabaseTypeToJavaType(column.getColumnValue(), index);
                            values[index] = value;
                    	}
                    	else {
                    		values[index] = null;
                    	}
                        
             
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
			value = type.fromString(value.toString());
		}
        return value;
    }
    /**
     * Diese Methode weist Datentypen einen Wert hinzu und liefert diesen zurück.
     * @param columnName - Name der Spalte
     * @return columnType - Wert des Datentyps
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
