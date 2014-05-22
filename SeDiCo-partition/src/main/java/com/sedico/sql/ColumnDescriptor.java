package com.sedico.sql;

import org.hibernate.dialect.Dialect;
import java.sql.Types;
/**
 * Diese Klasse implementiert einen ColumnDescriptors. Dieser setzt Spalten für die Spalten einen Spaltentyp.
 * @author jens
 *
 */
public class ColumnDescriptor {
    private final String columnName;
    private final int nativeColumnType;
    public int getNativeColumnType() {
		return nativeColumnType;
	}

	private long columnSize;
    private final int columnDigits;

    public ColumnDescriptor(String columnName, int nativeColumnType, long columnSize, int columnDigits) {
        this.columnName = columnName;
        this.nativeColumnType = nativeColumnType;
        
        //HACK: Oracle gibt für Number-Spalten als columnSize 0 und als Digits -127 an, wenn keine Größe definiert wurde
        //das macht aber kaum Sinn und ist laut Dokumentation auch gar nicht zulässig
        
        /*if(nativeColumnType == Types.INTEGER) {
            this.columnSize = 38;
            this.columnDigits = 127;
        }
        else {
            this.columnSize = columnSize;
            this.columnDigits = columnDigits;
        }*/
        
        switch (nativeColumnType) {
        	case Types.INTEGER: 	this.columnSize = 38;
            						this.columnDigits = 127;
            						break;
            
        	case Types.DATE: 		this.columnSize = 38;
									this.columnDigits = 127;
									break;
        	case Types.BIT:			this.columnSize = 1;
									this.columnDigits = 1;
									break;
        	case Types.VARCHAR: 	this.columnSize = 38;
									this.columnDigits = 127;
									break;
        
        default: this.columnDigits = columnDigits;
            
            
        }
    }

    public String getColumnName() {
        return columnName;
    }
/**
 * Diese Methode liefert den Datentyp zurück.
 * @param dialect - Dialect
 * @return type
 */
    public String getDataTypeForDialect(Dialect dialect) {
        String type;
        switch (nativeColumnType) {
            case Types.NUMERIC:
            case Types.DECIMAL:
            case Types.DOUBLE:
            case Types.FLOAT:
            case Types.REAL:
                type = dialect.getTypeName(this.nativeColumnType, 0, (int)this.columnSize, this.columnDigits);
                break;
            case Types.BIT:
            	type = dialect.getTypeName(-7);
            	break;
            case Types.DATE:
            	type = dialect.getTypeName(91);
            	break;
            case Types.TIMESTAMP:
            	type = dialect.getTypeName(93);
            	break;
            case Types.VARCHAR: type = dialect.getTypeName(this.nativeColumnType, 0, (int)this.columnSize, this.columnDigits);
            	break;
            case Types.CHAR: type = dialect.getTypeName(this.nativeColumnType, 0, (int)this.columnSize, this.columnDigits);
        		break;
            case Types.INTEGER: type = dialect.getTypeName(this.nativeColumnType, 0, (int)this.columnSize, this.columnDigits);
            	break;
            case Types.BOOLEAN: type = dialect.getTypeName(this.nativeColumnType, 0, (int)this.columnSize, this.columnDigits);
            	break;
            default:
            	type = "2000";
                //type = dialect.getTypeName(this.nativeColumnType, this.columnSize, this.columnDigits, 0);
        }
        return type;
    }
    
    public boolean isPrimaryKey() {
        return false;
    }
}
