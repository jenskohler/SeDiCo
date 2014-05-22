package com.sedico.sql;
/**
 * Diese abstrakte Klasse beschreibt die Datenbankverbindung zur SQL-Datenbank.
 * @author jens
 *
 */
public abstract class SqlConnectionDescriptor {
    private final String server;
    private final int port;
    private final String database;
    private final String table;
    private final String user;
    private final String password;
    /**
     * Diese Methode erzeugt die Datenbankverbindung zum SQL-Server.
     * @param server - Name des Servers	
     * @param port	- Portnummer
     * @param database - Name der Datenbank
     * @param table	-	Name der Tabelle
     * @param user	- Name des Users
     * @param password - Benutzerpasswort 
     * @param serverType - Typ des SQL Servers
     * @return MySqlConnectionDescriptor oder OracleConnectionDescriptor - Beschreiber der Datenbankverbindung 
     */
    public static SqlConnectionDescriptor create(String server, int port, String database, String table, String user, String password, SQLServers serverType) {
        switch (serverType) {
            case MySQL:
                return new MySqlConnectionDescriptor(server, port, database, table, user, password);
            case Oracle:
                return new OracleConnectionDescriptor(server, port, database, table, user, password);
        }
        throw new Error("Es gibt keinen passenden SQLConnectionDescriptor für diesen SQL-Typ.");
    }

    protected SqlConnectionDescriptor(String server, int port, String database, String table, String user, String password) {
        this.server = server;
        this.port = port;
        this.database = database;
        this.table = table;
        this.user = user;
        this.password = password;
    }

    public String getServer() {
        return server;
    }

    public int getPort() {
        return port;
    }

    public String getDatabase() {
        return database;
    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }

    public String getTable() {
        return table;
    }

    public abstract SQLServers getServerType();

    public abstract String getConnectionString();

    public abstract String getJdbcDriverName();

    public abstract String getDialectName();
}
