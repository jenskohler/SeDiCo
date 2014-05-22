package de.sedico.interfaces;

import java.util.NoSuchElementException;
import org.jclouds.rest.AuthorizationException;
import org.jclouds.compute.ComputeService;

/**
 * Diese Klasse implementiert das Interface ICOludServices. Es stellt Methoden für den Nutzerlogin, Bildschirmausgaben dar.
 * @author jens
 *
 */
public interface ICloudService {
	public String cloudLogin() throws AuthorizationException, NoSuchElementException;
	public String findCloudResources() throws NoSuchElementException;
	public ComputeService getComputeServiceContext(boolean logginOn);
	
	
	
}
