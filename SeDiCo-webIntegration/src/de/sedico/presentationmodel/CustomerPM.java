package de.sedico.presentationmodel;


import java.util.List;
import javax.ejb.EJB;

import javax.faces.bean.RequestScoped;
import javax.faces.bean.SessionScoped;
import javax.inject.Named;
import de.sedico.entities.Customer;
import de.sedico.remoteinterfaces.CustomerRemote;

@Named
@SessionScoped
/**
 * Diese Klasse implementiert den Kunden.
 * @author jens
 *
 */
public class CustomerPM {
	@EJB
	private CustomerRemote customerService;

	private Customer customer;		
	private String name;
	private String forename;
	
	
	public CustomerPM() {
		
	}

	//private Customer customerService;
	private List<Customer> getAllCustomers() {
		return  customerService.findAll();
	}
	/**
	 * Diese Methode liefert den Vor- und Nachnamen eines Kunden zurück.
	 * @param name - Name
	 * @param forename - Vorname
	 * @return costumer - Kunde
	 */
	private Customer getCustomerByNameAndForename(String name, String forename) {
		System.out.println("enter getCustomerByNameAndForename(...)");
		System.out.println(name);
		System.out.println(forename);
		//customer = customerService.findByNameAndForename(name, forename);
		
		return customer;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getForename() {
		return forename;
	}

	public void setForename(String forename) {
		this.forename = forename;
	}
	
	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}
	
	
}
