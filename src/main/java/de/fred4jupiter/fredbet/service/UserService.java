package de.fred4jupiter.fredbet.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.fred4jupiter.fredbet.domain.AppUser;
import de.fred4jupiter.fredbet.repository.AppUserRepository;

@Service
public class UserService {

	@Autowired
	private AppUserRepository appUserRepository;

	public AppUser createAndSaveUser(String username, String password, String... roles) {
		AppUser appUser = new AppUser(username, password, roles);
		return appUserRepository.save(appUser);
	}
}
