package de.sedico.cloudservices;

import java.io.Serializable;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

@ManagedBean
@SessionScoped

/**
 * Diese Klasse erzeugt eine generische Instanz, welche das Interface Serializable implementiert.
 * @author jens
 *
 */
public class GenericInstance implements Serializable {

	private static final long serialVersionUID = 1L;
	String publicIp;
	int port;
	String dbName;
	String partitionName;
	String dbUser;
	String dbPassword;
	String dbType;
	
	
	public String getPublicIp() {
		return publicIp;
	}
	public void setPublicIp(String publicIp) {
		this.publicIp = publicIp;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	public String getDbName() {
		return dbName;
	}
	public void setDbName(String dbName) {
		this.dbName = dbName;
	}
	public String getPartitionName() {
		return partitionName;
	}
	public void setPartitionName(String partitionName) {
		this.partitionName = partitionName;
	}
	public String getDbUser() {
		return dbUser;
	}
	public void setDbUser(String dbUser) {
		this.dbUser = dbUser;
	}
	public String getDbPassword() {
		return dbPassword;
	}
	public void setDbPassword(String dbPassword) {
		this.dbPassword = dbPassword;
	}
	public String getDbType() {
		return dbType;
	}
	public void setDbType(String dbType) {
		this.dbType = dbType;
	}
	
	
	
	
	
}
