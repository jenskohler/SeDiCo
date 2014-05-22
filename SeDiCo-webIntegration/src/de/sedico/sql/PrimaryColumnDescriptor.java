package de.sedico.sql;
/**
 * Diese Klasse beschreibt die primären Spalten. Sie erbt von der Klasse ColumnDescriptor.
 * @author jens
 *
 */
public class PrimaryColumnDescriptor extends ColumnDescriptor {

    public PrimaryColumnDescriptor(String columnName, int columnType, long columnSize, int columnDigits) {
        super(columnName, columnType, columnSize, columnDigits);
    }

    @Override
    public boolean isPrimaryKey() {
        return true;
    }
}
