package de.sedico.entities;

import java.io.Serializable;

import javax.faces.bean.ManagedBean;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@ManagedBean
public class User implements Serializable {
	private String username;
	private String password;
	
	@Id
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
	
	@Override 
	public String toString() {
		String retString = new String("User: " + username + " Password: " + password);
		return retString;
	}
	
	
}
