package de.sedico.actionControllers;

import java.io.File;
import java.io.Serializable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


import javax.faces.bean.RequestScoped;
import javax.faces.bean.SessionScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.log4j.Logger;

import de.sedico.generictableadapter.DBConnection;
import de.sedico.generictableadapter.TableColHeader;
import de.sedico.generictableadapter.TreeBean;
import de.sedico.partition.Configuration;



@ManagedBean(name = "loadDatabaseAction")
@SessionScoped

/**
 * Diese Klasse lädt die Aktionen, welche mit der Datenbank möglich sind.
 * @author jens
 */
public class LoadDatabaseAction implements Serializable {
	private static Logger log = Logger.getLogger(LoadDatabaseAction.class);
	private static final long serialVersionUID = 1L;
	private String chosenTable;
	
	private boolean connectButtonVisible = true;
	private boolean divideTableVisible = false;
	//TreeBean t = new TreeBean();
	
	
	public String getChosenTable() {
		return chosenTable;
	}
	public void setChosenTable(String chosenTable) {		
		this.chosenTable = chosenTable;
	}
	public boolean isConnectButtonVisible() {
		return connectButtonVisible;
	}
	public void setConnectButtonVisible(boolean connectButtonVisible) {
		this.connectButtonVisible = connectButtonVisible;
	}
	public boolean isDivideTableVisible() {
		return divideTableVisible;
	}
	public void setDivideTableVisible(boolean divideTableVisible) {
		this.divideTableVisible = divideTableVisible;
	}
	
	public String chooseTable() {
		return "/protected/protected_index.xhtml";
	}
	
	/*
	public String divideAction() {
		setConnectButtonVisible(true);
		setDivideTableVisible(false);
		
		Map<String,String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
		String user = params.get("user");
		String password = params.get("password");
		String host = params.get("host");
		String port = params.get("port");
		String chosenDatabase = params.get("chosenDatabase");
		
		log.info("LoadDatabaseAction: " + user + password + host + port + chosenDatabase);

		DBConnection dbCon = new DBConnection(host, port, chosenDatabase, user, password);
		
		
		List<TableColHeader> l = dbCon.getTableMetaData(chosenDatabase, chosenTable);
		for (int i=0; i<l.size(); i++) {
			log.info("LoadDataBaseAdapter: TreeBean ColHeaders:" +l.get(i).getLabel());
		}
		
		
		t.setTableColHeaderList(l);
		
		List content = dbCon.getTableData(chosenDatabase, chosenTable);
		Iterator i = content.iterator();
		while (i.hasNext()) {
			
			log.info("LoadDataBaseAdapter: content = " +i.next().toString());
		}
		
		log.info("LoadDatabaseAction: tableContentSize = " +content.size());
		t.setTableContent(content);
		
		
		
		return "/protected/tableDivide.xhtml";
	}
	*/
	/**
	 * Diese Methode kehr zur Auswahl der Datenbank zurück
	 * @return /protected/protected_index.xhtml
	 */
	
	public String backToChooseDatabaseAction() {
		setConnectButtonVisible(false);
		setDivideTableVisible(true);
		
		return "/protected/protected_index.xhtml";
	}
	
}
