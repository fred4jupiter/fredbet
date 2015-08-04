package de.fred4jupiter.fredbet.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import de.fred4jupiter.fredbet.domain.AppUser;
import de.fred4jupiter.fredbet.repository.AppUserRepository;
import de.fred4jupiter.fredbet.web.UserCommand;

@Service
public class UserService {

	@Autowired
	private AppUserRepository appUserRepository;

	public AppUser createAndSaveUser(String username, String password, String... roles) {
		AppUser appUser = appUserRepository.findByUsername(username);
		if (appUser == null) {
			appUser = new AppUser(username, password, roles);
			appUser = appUserRepository.save(appUser);
		}
		
		return appUser;
	}

	public List<AppUser> findAll() {
		return appUserRepository.findAll();
	}

	public UserCommand findByUserId(String userId) {
		AppUser appUser = appUserRepository.findOne(userId);
		if (appUser == null) {
			return null;
		}
		UserCommand userCommand = new UserCommand();
		userCommand.setUserId(appUser.getId());
		userCommand.setUsername(appUser.getUsername());
		for (GrantedAuthority grantedAuthority : appUser.getAuthorities()) {
			userCommand.addRole(grantedAuthority.getAuthority());
		}
		
		return userCommand;
	}

	public void save(UserCommand userCommand) {
		AppUser appUser = appUserRepository.findOne(userCommand.getUserId());
		if (appUser == null) {
			appUser = new AppUser(userCommand.getUsername(), userCommand.getPassword(), userCommand.getRoles());
		}
		
		appUser.setUsername(userCommand.getUsername());
		appUser.setPassword(userCommand.getPassword());
		appUser.setRoles(userCommand.getRoles());
		
		appUserRepository.save(appUser);
	}
}
