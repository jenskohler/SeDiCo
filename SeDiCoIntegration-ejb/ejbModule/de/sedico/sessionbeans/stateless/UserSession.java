package de.sedico.sessionbeans.stateless;

import java.util.List;

import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import de.sedico.entities.Customer;
import de.sedico.entities.User;
import de.sedico.localinterfaces.CustomerLocal;
import de.sedico.localinterfaces.UserLocal;
import de.sedico.remoteinterfaces.CustomerRemote;
import de.sedico.remoteinterfaces.UserRemote;

/**
 * Session Bean implementation class Customer
 */
@Stateless
@Remote(UserRemote.class)
@Local(UserLocal.class)
public class UserSession implements UserRemote, UserLocal {
	@PersistenceContext(unitName = "SeDiCo_Pool")
	private EntityManager em;
    /**
     * Default constructor. 
     */
    public UserSession() {
        
    }
	@Override
	public List<User> findAll() {
		List<User> allUsers =  (List<User>) em.createNativeQuery(
				"SELECT username, password FROM Users", User.class).getResultList();
		
		return allUsers;
	}
	@Override
	public User findByName(String name) {
		String query = new String("SELECT username, password FROM Users WHERE username = '" + name + "';");
		List<User> u = (List<User>)em.createNativeQuery(query, User.class).getResultList();
		return u.get(0);
	}
    
	
}
