package de.sedico.remoteinterfaces;

import java.util.List;

import javax.ejb.Remote;

import de.sedico.entities.Customer;
import de.sedico.entities.User;

@Remote
public interface UserRemote {

	List<User> findAll();
	User findByName(String name);

}
