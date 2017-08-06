package de.fred4jupiter.fredbet.service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

import de.fred4jupiter.fredbet.domain.AppUser;
import de.fred4jupiter.fredbet.domain.AppUserBuilder;
import de.fred4jupiter.fredbet.domain.ImageMetaData;
import de.fred4jupiter.fredbet.props.FredbetConstants;
import de.fred4jupiter.fredbet.props.FredbetProperties;
import de.fred4jupiter.fredbet.repository.AppUserRepository;
import de.fred4jupiter.fredbet.repository.BetRepository;
import de.fred4jupiter.fredbet.repository.ExtraBetRepository;
import de.fred4jupiter.fredbet.repository.ImageMetaDataRepository;
import de.fred4jupiter.fredbet.repository.PersistentLoginRepository;
import de.fred4jupiter.fredbet.repository.SessionTrackingRepository;
import de.fred4jupiter.fredbet.security.FredBetRole;
import de.fred4jupiter.fredbet.security.SecurityService;
import de.fred4jupiter.fredbet.web.profile.ChangePasswordCommand;
import de.fred4jupiter.fredbet.web.user.UserCommand;
import de.fred4jupiter.fredbet.web.user.UserDto;

@Service
@Transactional
public class UserService {

	private static final Logger LOG = LoggerFactory.getLogger(UserService.class);

	@Autowired
	private AppUserRepository appUserRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private FredbetProperties fredbetProperties;

	@Autowired
	private SecurityService securityService;

	@Autowired
	private ImageMetaDataRepository imageMetaDataRepository;

	@Autowired
	private BetRepository betRepository;

	@Autowired
	private ExtraBetRepository extraBetRepository;

	@Autowired
	private SessionTrackingRepository sessionTrackingRepository;

	@Autowired
	private PersistentLoginRepository persistentLoginRepository;

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

		if (isRoleSelectionDisabled(userCommand)) {
			LOG.debug("Role selection is disabled for user {}. Using default role {}", userCommand.getUsername(), FredBetRole.ROLE_USER);
			appUserBuilder.withRoles(Arrays.asList(FredBetRole.ROLE_USER.name()));
		} else {
			appUserBuilder.withRoles(userCommand.getRoles());
		}

		insertAppUser(appUserBuilder.build());
		return;
	}

	private boolean isRoleSelectionDisabled(UserCommand userCommand) {
		return securityService.isRoleSelectionDisabledForUser(userCommand.getUsername());
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
		Assert.notNull(userCommand.getUserId(), "userCommand.getUserId() must be given");
		AppUser appUser = appUserRepository.findOne(userCommand.getUserId());
		if (isRoleSelectionDisabled(userCommand)) {
			LOG.debug("Role selection is disabled for user {}. Do not update roles.", userCommand.getUsername());
		} else {
			appUser.setRoles(userCommand.getRoles());
		}

		if (userCommand.isResetPassword()) {
			appUser.setPassword(passwordEncoder.encode(fredbetProperties.getPasswordForReset()));
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

		imageMetaDataRepository.deleteMetaDataByOwner(appUser.getId());

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

	public List<UserDto> findAllAsUserDto() {
		List<ImageMetaData> metaDataList = imageMetaDataRepository.loadImageMetaDataOfUserProfileImageSet();

		Map<String, ImageMetaData> map = toMap(metaDataList);

		return findAll().stream().map(appUser -> toUserDto(appUser, map)).collect(Collectors.toList());
	}

	private Map<String, ImageMetaData> toMap(List<ImageMetaData> metaDataList) {
		Map<String, ImageMetaData> map = new HashMap<>();
		for (ImageMetaData imageMetaData : metaDataList) {
			map.put(imageMetaData.getOwner().getUsername(), imageMetaData);
		}

		return map;
	}

	private UserDto toUserDto(AppUser appUser, Map<String, ImageMetaData> map) {
		ImageMetaData userProfileImageMetaData = map.get(appUser.getUsername());
		if (userProfileImageMetaData != null) {
			return new UserDto(appUser.getId(), appUser.getUsername(), userProfileImageMetaData.getImageKey());
		} else {
			return new UserDto(appUser.getId(), appUser.getUsername());
		}
	}

	public void renameUser(String oldUsername, String newUsername) {
		if (FredbetConstants.TECHNICAL_USERNAME.equals(oldUsername)) {
			throw new RenameUsernameNotAllowedException("This user is the default admin user which username cannot be renamed!");
		}

		this.appUserRepository.renameUser(oldUsername, newUsername);
		this.betRepository.renameUser(oldUsername, newUsername);
		this.extraBetRepository.renameUser(oldUsername, newUsername);
		this.sessionTrackingRepository.renameUser(oldUsername, newUsername);
		this.persistentLoginRepository.renameUser(oldUsername, newUsername);
		
		this.securityService.getCurrentUser().setUsername(newUsername);
	}
}
