package de.sedico.cloudservices;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import org.apache.log4j.Logger;

@ManagedBean(name="amaInstance")
@SessionScoped

/**
 * Diese Klasse erzeugt eine Instanz der Amazon-Datenbank.
 * @author jens    
 *
 */
public class AmaInstance {

	private static final long serialVersionUID = 1L;
	private static Logger log = Logger.getLogger(AmaInstance.class);
	
	public AmaInstance() {
		log.info("AmaInstance: Object created");
	}
	
	
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
