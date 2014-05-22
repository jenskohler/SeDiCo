package de.sedico.sessionbeans.stateless;

import java.util.List;

import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import de.sedico.entities.Customer;
import de.sedico.localinterfaces.CustomerLocal;
import de.sedico.remoteinterfaces.CustomerRemote;

/**
 * Session Bean implementation class Customer
 */
@Stateless
@Remote(CustomerRemote.class)
@Local(CustomerLocal.class)
public class CustomerSession implements CustomerRemote, CustomerLocal {
	@PersistenceContext(unitName = "SeDiCo_Pool")
	private EntityManager em;
    /**
     * Default constructor. 
     */
    public CustomerSession() {
        
    }
    
	@Override
	public List<Customer> findAll() {
		List<Customer> allCustomers =  (List<Customer>) 
				em.createNativeQuery(
						"SELECT id, name, forename, dateOfBirth, creditCard, insuranceNo, street, zip, regCust, solScore " +
						"FROM Customer", Customer.class).getResultList();
		
		return allCustomers;
	}

	@Override
	public Customer findByNameAndForename(String name, String forename) {
		Customer cust = new Customer();
		
		em.createNativeQuery(
				"SELECT id, name, forename, dateOfBirth, creditCard, insuranceNo, street, zip, regCust, solScore " +
						" FROM Customer " +
						" WHERE name = " + name + " AND forename = " + forename, Customer.class).getResultList();
		return null;
	}

}
