package de.fred4jupiter.fredbet.data;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.fred4jupiter.fredbet.domain.AppUser;
import de.fred4jupiter.fredbet.service.UserService;

@Component
public class InitialUserPopulator {

	@Autowired
	private UserService userService;
	
	@PostConstruct
	public void createInitialUsers() {
		userService.save(new AppUser("admin", "admin", "ROLE_USER", "ROLE_ADMIN"));
		userService.save(new AppUser("test", "test", "ROLE_USER"));
	}
}
