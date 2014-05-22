package de.sedico.test;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import de.sedico.entities.Customer;
import de.sedico.remoteinterfaces.CustomerRemote;
import de.sedico.sessionbeans.stateless.CustomerSession;

/**
 * Diese Klasse testet die Main.
 * @author jens
 *
 */
public class TestMain {
	
    private static ArrayList customers;
    private static CustomerRemote c;
    private static Context context = null;
    private static Hashtable jndiProperties = new Hashtable();
	
	public static void main(String[] args) {
		
		try {
			
			c = lookup();

			
			customers = (ArrayList<Customer>) c.findAll();

			Iterator i = customers.iterator();

			System.out.println("List contains " + customers.size() + " customers:");

			while (i.hasNext()) {
				Customer cust = (Customer) i.next();
				System.out.println(cust.toString());

			}
		} catch (NamingException e) {
			e.printStackTrace();
		}

	}
/**
 * Diese Methode fügt verschiedenen Atrributen Werte hinzu und erzeugt einen String davon.
 * @return (CustomerRemote) context.lookup(lookupString) - String mit Werten
 * @throws NamingException - falls falscher Name vorhanden
 */
	private static CustomerRemote lookup() throws NamingException {
		jndiProperties.put(Context.URL_PKG_PREFIXES,
				"org.jboss.ejb.client.naming");
		jndiProperties.put(Context.INITIAL_CONTEXT_FACTORY,
				"org.jboss.as.naming.InitialContextFactory");
		jndiProperties.put(Context.PROVIDER_URL, "jnp://localhost:1099");
		context = new InitialContext(jndiProperties);

        // The app name is the application name of the deployed EJBs. This is typically the ear name
        // without the .ear suffix. However, the application name could be overridden in the application.xml of the
        // EJB deployment on the server.
        // Since we haven't deployed the application as a .ear, the app name for us will be an empty string
        final String appName = "SeDiCoEE";
        // This is the module name of the deployed EJBs on the server. This is typically the jar name of the
        // EJB deployment, without the .jar suffix, but can be overridden via the ejb-jar.xml
        // In this example, we have deployed the EJBs in a jboss-as-ejb-remote-app.jar, so the module name is
        // jboss-as-ejb-remote-app
        final String moduleName = "SeDiCo-ejb";
        // AS7 allows each deployment to have an (optional) distinct name. We haven't specified a distinct name for
        // our EJB deployment, so this is an empty string
        final String distinctName = "";
        // The EJB name which by default is the simple class name of the bean implementation class
        final String beanName = CustomerSession.class.getSimpleName();
        // the remote view fully qualified class name
        final String viewClassName = CustomerRemote.class.getName();
        // let's do the lookup
        final String lookupString = "ejb:" + appName + "/" + moduleName + "/" + distinctName +""+ beanName + "!" + viewClassName;
        System.out.println(lookupString);
        return (CustomerRemote) context.lookup(lookupString);
    }

 

}

