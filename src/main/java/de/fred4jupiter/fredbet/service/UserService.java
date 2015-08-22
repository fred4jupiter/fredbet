package de.fred4jupiter.fredbet.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import de.fred4jupiter.fredbet.domain.AppUser;
import de.fred4jupiter.fredbet.repository.AppUserRepository;
import de.fred4jupiter.fredbet.web.user.UserCommand;

@Service
public class UserService {

	private static final Logger LOG = LoggerFactory.getLogger(UserService.class);

	@Autowired
	private AppUserRepository appUserRepository;

	public List<AppUser> findAll() {
		return appUserRepository.findAll(new Sort(Direction.ASC, "username"));
	}

	public UserCommand findByUserId(String userId) {
		AppUser appUser = appUserRepository.findOne(userId);
		if (appUser == null) {
			return null;
		}
		UserCommand userCommand = new UserCommand();
		userCommand.setUserId(appUser.getId());
		userCommand.setUsername(appUser.getUsername());
		if (!CollectionUtils.isEmpty(appUser.getAuthorities())) {
			for (GrantedAuthority grantedAuthority : appUser.getAuthorities()) {
				userCommand.addRole(grantedAuthority.getAuthority());
			}
		}

		return userCommand;
	}

	public void save(UserCommand userCommand) {
		AppUser appUser = toAppUser(userCommand);
		if (appUser != null) {
			save(appUser);
		}
	}

	public void save(AppUser appUser) {
		appUserRepository.save(appUser);
	}

	private AppUser toAppUser(UserCommand userCommand) {
		Assert.notNull(userCommand.getUserId());
		AppUser appUser = appUserRepository.findOne(userCommand.getUserId());
		if (appUser == null) {
			LOG.error("Could not find user with userId: {}", userCommand.getUserId());
			return null;
		}

		appUser.setUsername(userCommand.getUsername());
		appUser.setPassword(userCommand.getPassword());
		appUser.setRoles(userCommand.getRoles());

		return appUser;
	}

	public void deleteUser(String userId) {
		appUserRepository.delete(userId);
	}
}
