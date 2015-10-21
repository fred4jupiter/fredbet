package de.fred4jupiter.fredbet.service;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import de.fred4jupiter.fredbet.FredBetRole;
import de.fred4jupiter.fredbet.domain.AppUser;
import de.fred4jupiter.fredbet.repository.AppUserRepository;
import de.fred4jupiter.fredbet.web.user.ChangePasswordCommand;
import de.fred4jupiter.fredbet.web.user.UserCommand;

@Service
public class UserService {

	private static final Logger LOG = LoggerFactory.getLogger(UserService.class);

	@Autowired
	private AppUserRepository appUserRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	public List<AppUser> findAll() {
		return appUserRepository.findAll(new Sort(Direction.ASC, "username"));
	}

	public AppUser findByAppUserId(String userId) {
		return appUserRepository.findOne(userId);
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

	public void save(AppUser appUser) {
		try {
			appUser.setPassword(passwordEncoder.encode(appUser.getPassword()));
			appUserRepository.save(appUser);
		} catch (DuplicateKeyException e) {
			LOG.info("user with username={} still exists. skipping save...", appUser.getUsername());
		}
	}

	public void deleteUser(String userId) {
		appUserRepository.delete(userId);
	}

	public void createOrUpdateUser(UserCommand userCommand) {
		if (StringUtils.isBlank(userCommand.getUserId())) {
			// create new user
			final AppUser adminUser = new AppUser(userCommand.getUsername(), userCommand.getPassword(), FredBetRole.ROLE_USER);
			save(adminUser);
			return;
		}

		Assert.notNull(userCommand.getUserId());
		AppUser appUser = appUserRepository.findOne(userCommand.getUserId());
		appUser.setUsername(userCommand.getUsername());
		appUser.setPassword(userCommand.getPassword());
		appUser.setRoles(userCommand.getRoles());
		save(appUser);
	}

	public void changePassword(AppUser appUser, ChangePasswordCommand changePasswordCommand) {
		if (appUser == null) {
			throw new IllegalArgumentException("Given user must not be null!");
		}

		if (!isCorrectOldPassword(appUser, changePasswordCommand)) {
			throw new OldPasswordWrongException("The old password is wrong!");
		}

		appUser.setPassword(changePasswordCommand.getNewPassword());
		save(appUser);
	}

	private boolean isCorrectOldPassword(AppUser appUser, ChangePasswordCommand changePasswordCommand) {
		return passwordEncoder.matches(changePasswordCommand.getOldPassword(), appUser.getPassword());
	}

}
