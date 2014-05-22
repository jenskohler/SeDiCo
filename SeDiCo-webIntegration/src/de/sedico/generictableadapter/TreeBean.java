package de.sedico.generictableadapter;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.faces.bean.SessionScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.component.UIOutput;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.AjaxBehaviorEvent;
import javax.servlet.ServletContext;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.log4j.Logger;
import org.primefaces.event.DragDropEvent;
import de.sedico.generictableadapter.DBConnection;
import de.sedico.partition.Configuration;
import de.sedico.tools.FacesUtil;



@ManagedBean(name="treeBean")
@SessionScoped
/**
 * Diese Klasse implementiert einen Baum und implementiert das Interface Serializable.
 * @author jens
 *
 */
public class TreeBean implements Serializable {
    private static Logger log = Logger.getLogger(TreeBean.class);
    private static final long serialVersionUID = 1L;
    private List<TableColHeader> tableColHeaderList = new ArrayList<TableColHeader>();; 
    
    private List<TableColHeader> tableColHeaderListPartition1 = new ArrayList<TableColHeader>();
    private List<TableColHeader> tableColHeaderListPartition2 = new ArrayList<TableColHeader>();

    
    /** Project-specific data structure. One list entry per one table row, Integer in Map is number of column */
    private List<Map<Integer, String>> tableContent;
    private List partition1 = new ArrayList();
    private List partition2 = new ArrayList();
    private String chosenTable;
    private int tableColHeaderListSize;
    private String sqlType;
	private String chosenDatabase;
    private String host;
    private int port;
    
	public String getChosenDatabase() {
		return chosenDatabase;
	}
	public void setChosenDatabase(String chosenDatabase) {
		this.chosenDatabase = chosenDatabase;
	}
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	

    public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	private String user;
    private String password;
    
    
    public String getSqlType() {
		return sqlType;
	}
	public void setSqlType(String sqlType) {
		this.sqlType = sqlType;
	}
	private String cloudProvider1;
    
    public String getCloudProvider1() {
		return cloudProvider1;
	}
    public void setCloudProvider1(String cloudProvider1) {
		this.cloudProvider1 = cloudProvider1;
	}
    private String cloudProvider2;
    
    public String getCloudProvider2() {
		return cloudProvider2;
	}
    public void setCloudProvider2(String cloudProvider2) {
		this.cloudProvider2 = cloudProvider2;
	}
    /**
     * Mit dieser Methode kann der gewählte Provider der ersten Partition geändert werden.
     * @param e - AjaxBehaviorEvent 
     */
    public void changeCloudProviderSelection1(AjaxBehaviorEvent e) {
    	log.info("Entering: changeCloudProviderSelection1 Event");
    	cloudProvider1 = ((UIOutput)e.getSource()).getValue().toString();
    	log.info("cloudProvider1: " +cloudProvider1);
    	
    }
    /**
     * Mit dieser Methode kann der gewählte Provider der zweiten Partition geändert werden.
     * @param e - AjaxBehaviorEvent 
     */
    public void changeCloudProviderSelection2(AjaxBehaviorEvent e) {
    	log.info("Entering: changeCloudProviderSelection2 Event");
    	cloudProvider2 = ((UIOutput)e.getSource()).getValue().toString();
    	log.info("cloudProvider2: " +cloudProvider2);
    	
    }
    /**
     * Diese Methode implementiert die Datenverteilung auf der ersten Partition.
     * @param ddEvent - DragDropEvent
     */
	public void onCarDrop1(DragDropEvent ddEvent) {  
    	
    	log.info("DragId: "+ ddEvent.getDragId());
    	log.info("DropId: "+ ddEvent.getDropId());
    	log.info("PhaseId: "+ ddEvent.getPhaseId());
    	log.info("Source: "+ ddEvent.getSource().toString());
    	
    	/*
    	String dragId = ddEvent.getDragId();

    	String componentId = dragId.substring(dragId.lastIndexOf(':') + 1);
    	
    	log.info("UIComponentID: "+componentId);
    	*/
    	
    	TableColHeader selectedContent = (TableColHeader) ddEvent.getData();  
    	
    	log.info("TreeBean: selectedContent = " +selectedContent.getLabel());
    	//add the primary key column to both partitions automatically
    	if (tableColHeaderListPartition1.isEmpty()) {
    		log.info("TreeBean: adding PK = " +tableColHeaderList.get(0));
    		tableColHeaderListPartition1.add(tableColHeaderList.get(0));
    		// if the second partition is not empty the primary key column can be deleted from the available data fieldset
    		if (! tableColHeaderListPartition2.isEmpty()) {
    			tableColHeaderList.remove(0);
    		}
    	}
    	
    	tableColHeaderListPartition1.add(selectedContent);
    	tableColHeaderList.remove(selectedContent);
    	
    	
    	/*

    	
    	//TableColHeader header = new TableColHeader(componentId, null);
    	
    	Iterator it = selectedContent.entrySet().iterator();
        
    	while (it.hasNext()) {
            Map.Entry pairs = (Map.Entry)it.next();
            //log.info(pairs.getKey() + " = " + pairs.getValue());
            //log.info("TableHeader: " +getTableHeaderIndex(componentId));
            if (pairs.getKey().equals(getTableHeaderIndex(componentId))) {
            	if (tableColHeaderListPartition1.size() == 0) {
            		log.info("TreeBean: adding PK to partition1 = "+tableColHeaderList.get(0));
            		tableColHeaderListPartition1.add(tableColHeaderList.get(0));
            	
            	}
            	
            	relevantContent1.put(getTableHeaderIndex(componentId), pairs.getValue().toString());
                log.info(getTableHeaderIndex(componentId) +" -> " +  pairs.getValue().toString());
                
            	
                
                TableColHeader tch = new TableColHeader(componentId, null);
                tableColHeaderListPartition1.add(tch);
                //log.info("TreeBean: Size relevantHeader = " +tableColHeaderListPartition1.size());
                
                partition1.add(relevantContent1);
                log.info("TreeBean: Size relevantContent1 = " +relevantContent1.size());
                Collection<String> s = relevantContent1.values();
                
                Iterator i = s.iterator();
                while (i.hasNext()) {
                	
                	log.info("TreeBean: relevantContent1 = " +i.next().toString());
                }
                
                
                log.info("remove: " +getTableHeaderIndex(componentId));
                tableColHeaderList.remove(getTableHeaderIndex(componentId));
            }
            
        }

    	
        //tableContent.remove(relevantContent); 
       */
        
    }  
    
    /**
     * Diese Methode implementiert die Datenverteilung der zweiten Partition
     * @param ddEvent - DragDropEvent
     */
    public void onCarDrop2(DragDropEvent ddEvent) {  
    	TableColHeader selectedContent = (TableColHeader) ddEvent.getData();  
    	
    	log.info("TreeBean: selectedContent = " +selectedContent.getLabel());
    	//add the primary key column to both partitions automatically
    	if (tableColHeaderListPartition2.isEmpty()) {
    		log.info("TreeBean: adding PK = " +tableColHeaderList.get(0));
    		tableColHeaderListPartition2.add(tableColHeaderList.get(0));
    		// if the first partition is not empty the primary key column can be deleted from the available data fieldset
    		if (! tableColHeaderListPartition1.isEmpty()) {
    			tableColHeaderList.remove(0);
    		}
    	}
    	
    	tableColHeaderListPartition2.add(selectedContent);
    	tableColHeaderList.remove(selectedContent);	
       
    }  
    /**
     * Diese Methode löscht die Inhalte aus den Partitionen
     * @return "/protected/protected_index.xhtml" - Verweis auf die leere Seite
     */
    public String clearPartitions() {
    	tableColHeaderListPartition1.clear();
    	tableColHeaderListPartition2.clear();
    	tableColHeaderList.clear();
    	
    	return "/protected/protected_index.xhtml";
    }
    /**
     * Diese Methode ändert den Cloud-Provider.
     * @return /protected/chooseClouds2.xhtml
     */
	public String chooseCloudProvider() {
		return "/protected/chooseClouds2.xhtml";
	}
    /**
     * Diese Methode holt die nötigen Informationen des Nutzers ein um die Cloud zu generieren.
     * @return /protected/tableDivide.xhtml - 
     */
    public String divideAction() {
    	Map<String,String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
		this.user = params.get("user");
		this.password = params.get("password");
		this.host = params.get("host");
		this.port = Integer.parseInt(params.get("port"));
		this.chosenTable = params.get("chosenTable");
		this.chosenDatabase = params.get("chosenDatabase");
		this.sqlType = params.get("sqlType");
		
		log.info("LoadDatabaseAction: " + user + password + host + port + chosenDatabase + chosenTable + sqlType);

		DBConnection dbCon = new DBConnection(host, String.valueOf(port), chosenDatabase, user, password, sqlType);
		
		
		List<TableColHeader> l = dbCon.getTableMetaData(chosenDatabase, chosenTable);
		for (int i=0; i<l.size(); i++) {
			log.info("LoadDataBaseAdapter: TreeBean ColHeaders:" +l.get(i).getLabel());
		}

		
		setTableColHeaderList(l);
		setTableColHeaderListSize(l.size());
		
		/*
		List content = dbCon.getTableData(chosenDatabase, chosenTable);
		Iterator i = content.iterator();
		while (i.hasNext()) {
			
			log.info("LoadDataBaseAdapter: content = " +i.next().toString());
		}
		
		log.info("LoadDatabaseAction: tableContentSize = " +content.size());
		//setTableContent(content);
		*/
		
		
		return "/protected/tableDivide.xhtml";
	
    }
    
    public String getChosenTable() {
		return chosenTable;
	}

	public void setChosenTable(String chosenTable) {
		
        //Configuration.table = chosenTable;
		
		this.chosenTable = chosenTable;
	}
    /**
     * Diese Mehode liefert den Index der Spaltennüberschrift
     * @param columnName - Spaltenname
     * @return returnValue - Index der Überschrift
     */
    public int getTableHeaderIndex(String columnName) {
    	int returnValue = -1;
    	Iterator<TableColHeader> i = tableColHeaderList.iterator();
    	while (i.hasNext()) {
    		TableColHeader colHeader = (TableColHeader)i.next();
    		if (colHeader.getLabel().equals(columnName)) {
    			returnValue = tableColHeaderList.indexOf(colHeader);
    		}    		
    	}
    	return returnValue;   	
    }

    
    public static TreeBean getCurrentInstance() {
    	return (TreeBean)FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("treeBean");
    	
    }
    
    
    public int getTableColHeaderIndex(TableColHeader tableColHeader) {
        return tableColHeaderList.indexOf(tableColHeader);
    } 
    public List<TableColHeader> getTableColHeaderList() {
        return tableColHeaderList;
    }
    public void setTableColHeaderList(List<TableColHeader> tableColHeaderList) {
        this.tableColHeaderList = tableColHeaderList;
    }
    public void setTableContent(List<Map<Integer, String>> tableContent) {
        this.tableContent = tableContent;
    }   
    public List<Integer> getTableContentIdKeysAsList(int id){
         return new ArrayList<Integer>(tableContent.get(id).keySet());
    }
	public List<Map<Integer, String>> getTableContent() {
		return tableContent;
	}
	public List<Map<Integer, String>> getPartition1() {
		return partition1;
	}
	public void setPartition1(List<Map<Integer, String>> partition1) {
		this.partition1 = partition1;
	}
	public List<Map<Integer, String>> getPartition2() {
		return partition2;
	}
	public void setPartition2(List<Map<Integer, String>> partition2) {
		this.partition2 = partition2;
	}
	public List<TableColHeader> getTableColHeaderListPartition1() {
		return tableColHeaderListPartition1;
	}
	public void setTableColHeaderListPartition1(
			List<TableColHeader> tableColHeaderListPartition1) {
		this.tableColHeaderListPartition1 = tableColHeaderListPartition1;
	}
    /**
     * Diese Mehode liefert den Index der Spaltennüberschrift der ersten Partition
     * @param columnName - Spaltenname
     * @return returnValue - Index der Überschrift
     */
    public int getTableHeaderPartition1Index(String columnName) {
    	int returnValue = -1;
    	Iterator<TableColHeader> i = tableColHeaderListPartition1.iterator();
    	while (i.hasNext()) {
    		TableColHeader colHeader = (TableColHeader)i.next();
    		if (colHeader.getLabel().equals(columnName)) {
    			returnValue = tableColHeaderListPartition1.indexOf(colHeader);
    		}    		
    	}
    	return returnValue;   	
    }
	
	public List<TableColHeader> getTableColHeaderListPartition2() {
		return tableColHeaderListPartition2;
	}
	public void setTableColHeaderListPartition2(
			List<TableColHeader> tableColHeaderListPartition2) {
		this.tableColHeaderListPartition2 = tableColHeaderListPartition2;
	}


	public int getTableColHeaderListSize() {
		return tableColHeaderListSize;
	}


	public void setTableColHeaderListSize(int tableColHeaderListSize) {
		this.tableColHeaderListSize = tableColHeaderListSize;
	}

}