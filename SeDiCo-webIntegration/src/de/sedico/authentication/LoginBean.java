package de.sedico.authentication;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;


import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.SessionScoped;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.security.auth.Subject;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.apache.log4j.Logger;

import de.sedico.generictableadapter.TreeBean;


@ManagedBean(name="loginBean")
@SessionScoped
/**
 * Diese Klasse simuliert den Login.
 * @author jens
 *
 */
public class LoginBean implements Serializable {
 
	private static final long serialVersionUID = 1L;
	private String username;
	private String password;
	private boolean loggedOn;
	private boolean loginFailed;
	private static Logger log = Logger.getLogger(LoginBean.class);



	/*
	 * This method logs the current user out of the web application
	 * @params String redirects to logout page
	 * @author Jens Kohler
	 * @version 0.1 
	 */
	
	/**
	 * Diese Methode meldet den aktuellen Nutzer ab
	 * @return /logout?faces-redirect=true
	 */
 
	public String logout() {
		//HttpServletRequest req = (HttpServletRequest)FacesContext.getCurrentInstance().getExternalContext().getRequest();
		//req.getSession().invalidate();
		log.info("Logout: Invalidate Session and delete user files");
		// invalidate session
		FacesContext fc = FacesContext.getCurrentInstance();
		 
		ExternalContext ec = fc.getExternalContext();
		
		ServletContext ctx = (ServletContext) ec.getContext();
		
		FacesContext context = FacesContext.getCurrentInstance();
	    
		
	    	HttpServletRequest request = (HttpServletRequest) fc.getExternalContext().getRequest();
			
			
			String username = request.getUserPrincipal().getName();
			
			String rootPath = ctx.getRealPath("/"); 
			String userDir = rootPath + "WEB-INF/zipPackage/"+request.getUserPrincipal().getName();
			File f = new File(userDir); 
			del(f);
			
			f = new File(userDir+".zip");
			if (f != null) {
				f.delete();
			}

		ec.invalidateSession();
	
	    
  
		return "/logout?faces-redirect=true";
		
	}
	/**
	 * Diese Methode löscht eine Datei
	 * @param dir - Datei
	 * @return dir.delete(), - Datei wird gelöscht, falls die Datei nicht leer ist
	 */
	private boolean del(File dir){
		if (dir != null) {
			if (dir.isDirectory()) {
				File[] files = dir.listFiles();
				
				for (File aktFile: files){
					del(aktFile);
				}
				
			}
			return dir.delete();
			
		}
		
		return false;
		
		
	}
	
	
	public String getUsername() {
		return username;
	}
 
	public void setUsername(String username) {
		this.username = username;
	}
 

	
	public boolean isLoggedOn() {
		return loggedOn;
	}

	public void setLoggedOn(boolean loggedOn) {
		this.loggedOn = loggedOn;
	}

	public boolean isLoginFailed() {
		return loginFailed;
	}

	public void setLoginFailed(boolean loginFailed) {
		this.loginFailed = loginFailed;
	}
}