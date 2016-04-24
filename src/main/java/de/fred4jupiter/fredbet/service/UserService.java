package de.fred4jupiter.fredbet.service;

import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import de.fred4jupiter.fredbet.FredbetConstants;
import de.fred4jupiter.fredbet.domain.AppUser;
import de.fred4jupiter.fredbet.domain.AppUserBuilder;
import de.fred4jupiter.fredbet.repository.AppUserRepository;
import de.fred4jupiter.fredbet.security.FredBetRole;
import de.fred4jupiter.fredbet.web.user.ChangePasswordCommand;
import de.fred4jupiter.fredbet.web.user.UserCommand;

@Service
@Transactional
public class UserService {

	private static final Logger LOG = LoggerFactory.getLogger(UserService.class);

	@Autowired
	private AppUserRepository appUserRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	public List<AppUser> findAll() {
		return appUserRepository.findAll(new Sort(Direction.ASC, "username"));
	}

	public AppUser findByAppUserId(Long userId) {
		return appUserRepository.findOne(userId);
	}

	public UserCommand findByUserId(Long userId) {
		AppUser appUser = appUserRepository.findOne(userId);
		if (appUser == null) {
			return null;
		}
		UserCommand userCommand = new UserCommand();
		userCommand.setUserId(appUser.getId());
		userCommand.setUsername(appUser.getUsername());
		userCommand.setDeletable(appUser.isDeletable());
		if (!CollectionUtils.isEmpty(appUser.getAuthorities())) {
			for (GrantedAuthority grantedAuthority : appUser.getAuthorities()) {
				userCommand.addRole(grantedAuthority.getAuthority());
			}
		}

		return userCommand;
	}

	public void createUser(UserCommand userCommand) {
		// create new user
		AppUserBuilder appUserBuilder = AppUserBuilder.create().withUsernameAndPassword(userCommand.getUsername(),
				userCommand.getPassword());

		if (userCommand.isRoleSelectionDisabled()) {
			LOG.debug("Role selection is disabled for user {}. Using default role {}", userCommand.getUsername(), FredBetRole.ROLE_USER);
			appUserBuilder.withRoles(Arrays.asList(FredBetRole.ROLE_USER.name()));
		} else {
			appUserBuilder.withRoles(userCommand.getRoles());
		}

		insertAppUser(appUserBuilder.build());
		return;
	}

	public void insertAppUser(AppUser appUser) throws UserAlreadyExistsException {
		AppUser foundUser = appUserRepository.findByUsername(appUser.getUsername());
		if (foundUser != null) {
			throw new UserAlreadyExistsException("User with username=" + appUser.getUsername() + " already exists.");
		}

		appUser.setPassword(passwordEncoder.encode(appUser.getPassword()));
		appUserRepository.save(appUser);
	}

	public AppUser updateUser(UserCommand userCommand) {
		Assert.notNull(userCommand.getUserId());
		AppUser appUser = appUserRepository.findOne(userCommand.getUserId());
		if (userCommand.isRoleSelectionDisabled()) {
			LOG.debug("Role selection is disabled for user {}. Do not update roles.", userCommand.getUsername());
		} else {
			appUser.setRoles(userCommand.getRoles());
		}

		if (userCommand.isResetPassword()) {
			appUser.setPassword(passwordEncoder.encode(FredbetConstants.DEFAULT_PASSWORD_ON_RESET));
		}

		updateAppUser(appUser);
		return appUser;
	}

	void updateAppUser(AppUser appUser) {
		AppUser userToBeUpdated = appUserRepository.findOne(appUser.getId());
		if (userToBeUpdated == null) {
			throw new IllegalArgumentException(
					"Given user with username=" + appUser.getUsername() + " does not exists. ID=" + appUser.getId());
		}

		userToBeUpdated.setRoles(appUser.getRoles());
		appUserRepository.save(appUser);
	}

	public void deleteUser(Long userId) {
		AppUser appUser = appUserRepository.findOne(userId);
		if (appUser == null) {
			LOG.info("Could not find user with id={}", userId);
			return;
		}

		if (!appUser.isDeletable()) {
			throw new UserNotDeletableException("Could not delete user with name={}, because its marked as not deletable");
		}

		appUserRepository.delete(userId);
	}

	public void changePassword(Long userId, ChangePasswordCommand changePasswordCommand) {
		AppUser appUser = appUserRepository.findOne(userId);
		if (appUser == null) {
			throw new IllegalArgumentException("Could not found user with userId=" + userId);
		}

		final String enteredOldPasswordPlain = changePasswordCommand.getOldPassword();
		final String oldSavedEncryptedPassword = appUser.getPassword();

		if (!passwordEncoder.matches(enteredOldPasswordPlain, oldSavedEncryptedPassword)) {
			throw new OldPasswordWrongException("The old password is wrong!");
		}

		appUser.setPassword(passwordEncoder.encode(changePasswordCommand.getNewPassword()));
		appUserRepository.save(appUser);
	}

}
