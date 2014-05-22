package com.sedico;

import com.sedico.partition.*;
import com.sedico.sql.*;
import org.apache.commons.configuration.*;
import java.util.*;
/**
 * Diese Klasse implementiert eine Configuration.
 * @author jens
 *@version 1.0 
 */
public class Configuration {
    private final static String configurationFileName = "sedico.config";
    private static List<PartitionDescriptor> partitionDescriptors;
    /**
     * Diese Methode fügt der Konfiguration die übergebenen Paramter hinzu
     * @param name - Name der Konfiguration
     * @param publicIP	- öffentliche IP der Konfiguration
     * @param port - Portnummer	
     * @param database - Name der Datenbank
     * @param table	- 	Name der Tabelle
     * @param user	-	Benutzer
     * @param password	-	Benutzerpasswort
     * @param serverType	-	Typ des Servers
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
     * Diese Methode erzeugt einen SQLConnectionDescriptor zurück 
     * @return SqlConnectionDescriptor.create(server, port, database, table, user, password, serverType);
     */
    public static SqlConnectionDescriptor getSource() {
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
     * Diese Methode liefert einen Spaltenbeschreiber zurück
     * @return partitionDescriptors - Liste der Spaltenbeschreiber
     */
    public static List<PartitionDescriptor> getPartitions() {
        if (partitionDescriptors == null) {
            partitionDescriptors = initializePartitions();
        }
        return partitionDescriptors;
    }
    /**
     * Diese Methode liefert die primären Spaltenbeschreiber zurück
     * @return getPartitions().get(0) - die Partitionen am Index 0 
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
     * Diese Methode liefert die sekundären Spaltenbeschreibers zurück
     * @return getPartitions().get(1) - die Partitionen am Index 1
     * 
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
     * Diese Methode initialisiert eine Partition und liefert diese in einer Liste zurück
     * @return result - Liste der initalisierten Spaltenbeschreiber
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
     * Diese Methode prüft auf primäre Spaltenbeschreiber
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
     * Diese Methode liest die Daten für den Spaltenbeschreiber ein und erzeugt einen Spaltenbeschreiber
     * @param config - XMLConfiguration
     * @param partitions - Liste der Partitionen
     * @param partitionCounter - Counter
     * @return partition - erzeugter Spaltenbeschreiber
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
