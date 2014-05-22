package de.sedico.localinterfaces;

import java.util.List;

import javax.ejb.Local;

import de.sedico.entities.Customer;

@Local
public interface CustomerLocal {
	List<Customer> findAll();
}
