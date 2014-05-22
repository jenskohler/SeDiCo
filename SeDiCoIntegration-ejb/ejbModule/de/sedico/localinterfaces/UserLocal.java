package de.sedico.localinterfaces;

import java.util.List;

import javax.ejb.Local;

import de.sedico.entities.User;

@Local
public interface UserLocal {
	List<User> findAll();
}