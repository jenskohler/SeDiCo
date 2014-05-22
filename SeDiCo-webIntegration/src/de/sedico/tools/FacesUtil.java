package de.sedico.tools;

import java.util.Map;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.apache.log4j.Logger;

import de.sedico.generictableadapter.TreeBean;
/**
 * Hier wird die Klasse FacesUtil implementiert.
 * @author jens
 *
 */
public class FacesUtil {
	 private static Logger log = Logger.getLogger(FacesUtil.class);
    // Helpers -----------------------------------------------------------------------------------
/**
 * Diese Methode liefert Schlüssel zurück, welche mit dem Übergabeparameter übereinstimmen.
 * @param bean - Object
 * @return key - übereinstimmender Schlüssel
 */
    public static String lookupManagedBeanName(Object bean) {
        ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
        log.info("Searching context for Bean: " +bean);
        // Get requestmap.
        Map<String, Object> requestMap = externalContext.getRequestMap();
    
        // Lookup the current bean instance in the request scope.
        for (String key : requestMap.keySet()) {
        	log.info("RequestScoped Beans: " +key);
        	if (bean.equals(requestMap.get(key))) {
                // The key is the managed bean name.
            	
                return key;
            }
        }
    
        // Bean is not in the request scope. Get the sessionmap then.
        Map<String, Object> sessionMap = externalContext.getSessionMap();

        // Lookup the current bean instance in the session scope.
        for (String key : sessionMap.keySet()) {
        	log.info("SessionScoped Beans: " +key);
        	if (bean.equals(sessionMap.get(key))) {
                // The key is the managed bean name.
                return key;
            }
        }

        // Bean is also not in the session scope. Get the applicationmap then.
        Map<String, Object> applicationMap = externalContext.getApplicationMap();

        // Lookup the current bean instance in the application scope.
        for (String key : applicationMap.keySet()) {
        	log.info("ApplicationScoped Beans: " +key);
            if (bean.equals(applicationMap.get(key))) {
                // The key is the managed bean name.
                return key;
            }
        }

        // Bean is also not in the application scope.
        // Is the bean's instance actually a managed bean instance then? =)
        return null;
    }

}
