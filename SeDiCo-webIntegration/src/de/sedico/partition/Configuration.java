package de.sedico.partition;

import de.sedico.partition.*;
import de.sedico.sql.*;
import org.apache.commons.configuration.*;
import java.util.*;

import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
/**
 * Diese Klasse stellt eine Configuration bereit.
 * @author jens
 *
 */
public class Configuration {
	
	
    private static String configurationFileName;
    
    public Configuration() {
    	FacesContext fc = FacesContext.getCurrentInstance();
    	HttpServletResponse response = (HttpServletResponse) fc
    			.getExternalContext().getResponse();

		ServletContext ctx = (ServletContext) FacesContext.getCurrentInstance()
				.getExternalContext().getContext();
		String rootPath = ctx.getRealPath("/");

		HttpServletRequest request = (HttpServletRequest) fc
				.getExternalContext().getRequest();

		configurationFileName = rootPath + "WEB-INF/zipPackage/"
				+ request.getUserPrincipal().getName()
				+ "/sedico.config";
    }
    
    
    public static String getConfigurationfilename() {

    	return configurationFileName;
	}

	private static List<PartitionDescriptor> partitionDescriptors;
	/**
	 * Diese Methode fügt eine Zielkonfiguration hinzu.
	 * @param name - Hostname
	 * @param publicIP - Portname
	 * @param port - Portnummer
	 * @param database - Name der Datenbank
	 * @param table - Name der Tabelle
	 * @param user - Benutzername
	 * @param password - Passwort
	 * @param serverType - Datenbanktyp
	 */
    public static void addTargetConfiguration(String name, String publicIP, int port, String database, String table,
                                              String user, String password, SQLServers serverType) {
        try {
            XMLConfiguration config = new XMLConfiguration(configurationFileName);
            config.addProperty("targets.target(-1).name", name);
            config.addProperty("targets.target.publicIP", publicIP);
            config.addProperty("targets.target.port", port);
            config.addProperty("targets.target.database", database);
            config.addProperty("targets.target.table", table);
            config.addProperty("targets.target.user", user);
            config.addProperty("targets.target.password", password);
            config.addProperty("targets.target.sqlType", serverType);
            config.addProperty("targets.target.partition.column", "sampleColumnName");
            config.save();
        }
        catch(ConfigurationException e) {
            System.out.println(e.getMessage());
        }
    }
    /**
     * Diese Methode erzeugt eine SqlConnectionDescriptor und liefert ihn zurück.
     * @return SqlConnectionDescriptor - beschreibt die Verbindung zum SQL-Server
     */
    public static SqlConnectionDescriptor getSource() {
    	FacesContext fc = FacesContext.getCurrentInstance();
    	HttpServletResponse response = (HttpServletResponse) fc
    			.getExternalContext().getResponse();

		ServletContext ctx = (ServletContext) FacesContext.getCurrentInstance()
				.getExternalContext().getContext();
		String rootPath = ctx.getRealPath("/");

		HttpServletRequest request = (HttpServletRequest) fc
				.getExternalContext().getRequest();

		configurationFileName = rootPath + "WEB-INF/zipPackage/"
				+ request.getUserPrincipal().getName()
				+ "/sedico.config";
    	XMLConfiguration config;
        try {
            config = new XMLConfiguration(configurationFileName);
        }
        catch(ConfigurationException e) {
            System.out.println(e.getMessage());
            return null;
        }
        String server = config.getString("source.publicIP");
        int port = config.getInt("source.port");
        String database = config.getString("source.database");
        String table = config.getString("source.table");
        String user = config.getString("source.user");
        String password = config.getString("source.password");
        SQLServers serverType = Enum.valueOf(SQLServers.class, config.getString("source.sqlType"));
        return SqlConnectionDescriptor.create(server, port, database, table, user, password, serverType);
    }
    /**
     * Diese Methode liefert die Partitionen zurück
     * @return partitionDescriptors - Liste von Spaltenbeschreibern
     */
    public static List<PartitionDescriptor> getPartitions() {
        if (partitionDescriptors == null) {
            partitionDescriptors = initializePartitions();
        }
        return partitionDescriptors;
    }
    /**
     * Diese Methode liefert die primären Partitionen zurück
     * @return descriptor - Spaltenbeschreiber
     */
    public static PartitionDescriptor getPrimaryPartition() {
        for(PartitionDescriptor descriptor : getPartitions()) {
            if (descriptor.isPrimary()) {
                return descriptor;
            }
        }
        return getPartitions().get(0);
    }
    /**
     * Diese Methode liefert die sekundären Partitionen zurück
     * @return descriptor - Spaltenbeschreiber
     */
    public static PartitionDescriptor getSecondaryPartition() {
        for(PartitionDescriptor descriptor : getPartitions()) {
            if (!descriptor.isPrimary()) {
                return descriptor;
            }
        }
        return getPartitions().get(1);
    }

    /**
     * Diese Methode initialisiert die Partitionen.
     * @return result - Liste von Spaltenbeschreibern
     */
    private static List<PartitionDescriptor> initializePartitions() {
        List<PartitionDescriptor> result = new ArrayList();
        int partitionCounter = 0;
        XMLConfiguration config;
        try {
            config = new XMLConfiguration(configurationFileName);
        }
        catch(ConfigurationException e) {
            System.out.println(e.getMessage());
            return result;
        }

        List<Object> partitions = config.getList("targets.target(" + partitionCounter + ").partition.column");
        while(partitions.size() > 0) {
            PartitionDescriptor partition = readPartitionDescriptor(config, partitions, partitionCounter);
            result.add(partition);
            partitionCounter++;
            partitions = config.getList("targets.target(" + partitionCounter + ").partition.column");
        }
        checkForPrimaryPartition(result);
        return result;
    }
    /**
     * Diese Methode prüft die Partitionen, ob sie primär sind.
     * @param partitionDescriptors - Liste von Spaltenbeschreibern
     */
    private static void checkForPrimaryPartition(List<PartitionDescriptor> partitionDescriptors) {
        int foundPrimaries = 0;
        for(PartitionDescriptor descriptor : partitionDescriptors) {
            if (descriptor.isPrimary()) {
                foundPrimaries++;
            }
        }

        if (foundPrimaries == 0) {
            //Wenn wir hier gelandet sind, gab es keine primäre Partition, dann einfach die 1. Partition als solche markieren
            PartitionDescriptor descriptor = partitionDescriptors.get(0);
            SqlConnectionDescriptor sqlDescriptor = SqlConnectionDescriptor.create(
                    descriptor.getServer(),
                    descriptor.getPort(),
                    descriptor.getDatabase(),
                    descriptor.getTable(),
                    descriptor.getUser(),
                    descriptor.getPassword(),
                    descriptor.getServerType());
            PartitionDescriptor newDescriptor = new PartitionDescriptor(descriptor.getName(), true, descriptor.getInstanceID(), descriptor.getColumns(), sqlDescriptor);
            partitionDescriptors.remove(0);
            partitionDescriptors.add(0, newDescriptor);
        }
        else if (foundPrimaries > 1) {
            throw new Error("Es sind mehr als eine Partition als die primäre Partition konfiguriert.");
        }
    }
    /**
     * Diese Methode liest die Daten des Spaltenbeschreibers ein.
     * @param config - XMLConfiguration
     * @param partitions - Liste von Objekten 
     * @param partitionCounter - int
     * @return partition - Spaltenbeschreiber
     */
    private static PartitionDescriptor readPartitionDescriptor(XMLConfiguration config, List<Object> partitions, int partitionCounter) {
        final String baseKey = "targets.target(" + partitionCounter + ").";
        boolean isPrimary = false;
        try {
            isPrimary = config.getBoolean(baseKey + "[@isPrimary]");
        }
        catch (NoSuchElementException e) {
        }
        String name = config.getString(baseKey + "name");
        String publicIP = config.getString(baseKey + "publicIP");
        String instanceID = config.getString(baseKey + "instanceID");
        int port = config.getInt(baseKey + "port");
        String database = config.getString(baseKey + "database");
        String table = config.getString(baseKey + "table");
        String user = config.getString(baseKey + "user");
        String password = config.getString(baseKey + "password");
        SQLServers serverType = Enum.valueOf(SQLServers.class, config.getString(baseKey + "sqlType"));
        List<String> partitionColumns = new ArrayList();
        for(Object value : partitions) {
            partitionColumns.add(value.toString());
        }
        SqlConnectionDescriptor connectionDescriptor = SqlConnectionDescriptor.create(publicIP, port, database, table, user, password, serverType);
        PartitionDescriptor partition = new PartitionDescriptor(name, isPrimary, instanceID, partitionColumns, connectionDescriptor);
        return partition;
    }
}
