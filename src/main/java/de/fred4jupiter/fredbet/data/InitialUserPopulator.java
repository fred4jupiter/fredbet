package de.fred4jupiter.fredbet.data;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.fred4jupiter.fredbet.service.UserService;

@Component
public class InitialUserPopulator {

	@Autowired
	private UserService userService;
	
	@PostConstruct
	public void createInitialUsers() {
		userService.createAndSaveUser("admin", "admin", "ROLE_USER", "ROLE_ADMIN");
		userService.createAndSaveUser("michael", "michael", "ROLE_USER");
	}
}
