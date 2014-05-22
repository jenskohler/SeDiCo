package com.sedico.sql;
/**
 * Diese Klasse beschreibt die Verbindung zur Oracle-Datenbank. Sie erbt von der Klasse SqlConnectionDescriptor.
 * @author jens
 *
 */
public class OracleConnectionDescriptor extends SqlConnectionDescriptor {

    protected OracleConnectionDescriptor(String server, int port, String database, String table, String user, String password) {
        super(server, port, database, table, user, password);
    }

    @Override
    public String getDatabase() {
        return super.getDatabase().toUpperCase();
    }

    @Override
    public String getTable() {
        return super.getTable().toUpperCase();
    }

    @Override
    public SQLServers getServerType() {
        return SQLServers.Oracle;
    }

    @Override
    public String getConnectionString() {
        return String.format("jdbc:oracle:thin:@%s:%s:XE", getServer(), getPort());
    }

    @Override
    public String getJdbcDriverName() {
        return "oracle.jdbc.OracleDriver";
    }

    @Override
    public String getDialectName() {
        return "org.hibernate.dialect.Oracle10gDialect";
    }
}
