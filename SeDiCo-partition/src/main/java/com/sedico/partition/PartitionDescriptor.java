package com.sedico.partition;

import com.sedico.sql.*;
import org.hibernate.cfg.Configuration;

import java.util.*;
/**
 * Diese Klasse erzeugt einen Spaltenbeschreiber.
 * @author jens
 *
 */
public class PartitionDescriptor {
    private final String name;
    private final boolean isPrimary;
    private final String instanceID;
    private final SqlConnectionDescriptor sqlConnection;
    private final List<String> columns;

    public PartitionDescriptor(String name, boolean isPrimary, String instanceID, List<String> columns, SqlConnectionDescriptor sqlConnection) {
        this.name = name;
        this.isPrimary = isPrimary;
        this.instanceID = instanceID;
        this.sqlConnection = sqlConnection;
        this.columns = Collections.unmodifiableList(columns);
    }

    public String getName() {
        return name;
    }

    public boolean isPrimary() {
        return isPrimary;
    }

    public String getInstanceID() {
        return instanceID;
    }

    public String getServer() {
        return sqlConnection.getServer();
    }

    public int getPort() {
        return sqlConnection.getPort();
    }

    public String getDatabase() {
        return sqlConnection.getDatabase();
    }

    public String getTable() {
        return sqlConnection.getTable();
    }

    public String getUser() {
        return sqlConnection.getUser();
    }

    public String getPassword() {
        return sqlConnection.getPassword();
    }

    public SQLServers getServerType() {
        return sqlConnection.getServerType();
    }

    public String getConnectionString() {
        return sqlConnection.getConnectionString();
    }

    public String getJdbcDriverName() {
        return sqlConnection.getJdbcDriverName();
    }

    public String getDialectName() {
        return sqlConnection.getDialectName();
    }

    public List<String> getColumns() {
        return columns;
    }

    @Override
    public String toString() {
        return name;
    }
}
