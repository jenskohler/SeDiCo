package de.sedico.generictableadapter;

import java.io.Serializable;
import java.util.ArrayList;

import javax.faces.bean.RequestScoped;
import javax.faces.bean.SessionScoped;
import javax.faces.bean.ManagedBean;

import com.sedico.generictableadapter.ColumnType;


@ManagedBean(name = "genericTableAdapter")
@SessionScoped
/**
 * Mit dieser Klasse können die Spalten und Zeilen gezählt werden, sowie der Tabelleninhalt ausgegeben. Sie implementiert das Interface Serializable.
 * @author jens
 *
 */
public class GenericTableAdapter implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private String 							tableName;
	private int 							columnCount;
	private int 							rowCount;
	private ArrayList<ColumnType> 	columnTypes;
	private ArrayList<Object> 		tableContent;
	
	
	public String getTableName() {
		return tableName;
	}
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	public int getColumnCount() {
		return columnCount;
	}
	public void setColumnCount(int columnCount) {
		this.columnCount = columnCount;
	}
	public int getRowCount() {
		return rowCount;
	}
	public void setRowCount(int rowCount) {
		this.rowCount = rowCount;
	}
	public ArrayList<ColumnType> getColumnType() {
		return columnTypes;
	}
	public void setColumnType(ArrayList<ColumnType> columnTypes) {
		this.columnTypes = columnTypes;
	}
	public ArrayList<Object> getTableContent() {
		return tableContent;
	}

	public void setTableContent(ArrayList<Object> tableContent) {
		this.tableContent = tableContent;
	}
	
	

		
		
	
}
