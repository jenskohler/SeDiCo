package com.sedico.sql;
/**
 * Diese Klasse beschreibt die primären Spalten. Sie erbt von der Klasse ColumnDescriptor, welche die Spalten beschreibt.
 * @author jens
 *
 */
public class PrimaryColumnDescriptor extends ColumnDescriptor {

    public PrimaryColumnDescriptor(String columnName, int columnType, long columnSize, int columnDigits) {
        super(columnName, columnType, columnSize, columnDigits);
    }
    /**
     * Diese Methode liefert true zurück, falls es sich um die primäre Spalte handelt.
     */
    @Override
    public boolean isPrimaryKey() {
        return true;
    }
}
