package de.sedico.sql;

import de.sedico.partition.SQLServers;
/**
 * Diese Klasse beschreibt die Verbindung zum MySQL-Server. Sie implementiert das Interface SqlConnectionDescriptor.
 * @author jens
 *
 */
public class MySqlConnectionDescriptor extends SqlConnectionDescriptor {

    protected MySqlConnectionDescriptor(String server, int port, String database, String table, String user, String password) {
        super(server, port, database, table, user, password);
    }

    @Override
    public SQLServers getServerType() {
        return SQLServers.MySQL;
    }

    @Override
    public String getConnectionString() {
        return String.format("jdbc:mysql://%s:%s/%s", getServer(), getPort(), getDatabase());
    }

    @Override
    public String getJdbcDriverName() {
        return "com.mysql.jdbc.Driver";
    }

    @Override
    public String getDialectName() {
        return "org.hibernate.dialect.MySQL5Dialect";
    }
}

