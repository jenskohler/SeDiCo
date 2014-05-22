package de.sedico.sql;

import org.hibernate.dialect.Dialect;
import java.sql.Types;
/**
 * Diese Klasse beschreibt die Spalte einer Tabelle.
 * @author jens
 *
 */
public class ColumnDescriptor {
    private final String columnName;
    private final int nativeColumnType;
    private final long columnSize;
    private final int columnDigits;

    public ColumnDescriptor(String columnName, int nativeColumnType, long columnSize, int columnDigits) {
        this.columnName = columnName;
        this.nativeColumnType = nativeColumnType;

        //HACK: Oracle gibt für Number-Spalten als columnSize 0 und als Digits -127 an, wenn keine Größe definiert wurde
        //das macht aber kaum Sinn und ist laut Dokumentation auch gar nicht zulässig
        if(nativeColumnType == Types.INTEGER) {
            this.columnSize = 38;
            this.columnDigits = 127;
        }
        else {
            this.columnSize = columnSize;
            this.columnDigits = columnDigits;
        }
    }

    public String getColumnName() {
        return columnName;
    }
    /**
     * Diese Methode wandelt verschiedene Datentypen in Strings um.
     * @param dialect - übergebener Datentyp
     * @return type -String
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
            default:
                type = dialect.getTypeName(this.nativeColumnType, this.columnSize, this.columnDigits, 0);
        }
        return type;
    }

    public boolean isPrimaryKey() {
        return false;
    }
}
