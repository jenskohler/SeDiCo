package de.sedico.remoteinterfaces;

import java.util.List;

import javax.ejb.Remote;

import de.sedico.entities.Customer;

@Remote
public interface CustomerRemote {

	List<Customer> findAll();
	Customer findByNameAndForename(String name, String forename);

}
