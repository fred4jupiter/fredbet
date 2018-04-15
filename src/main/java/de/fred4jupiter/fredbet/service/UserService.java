package de.fred4jupiter.fredbet.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import de.fred4jupiter.fredbet.domain.AppUser;
import de.fred4jupiter.fredbet.domain.ImageMetaData;
import de.fred4jupiter.fredbet.props.CacheNames;
import de.fred4jupiter.fredbet.props.FredbetConstants;
import de.fred4jupiter.fredbet.repository.AppUserRepository;
import de.fred4jupiter.fredbet.repository.BetRepository;
import de.fred4jupiter.fredbet.repository.ExtraBetRepository;
import de.fred4jupiter.fredbet.repository.ImageMetaDataRepository;
import de.fred4jupiter.fredbet.repository.SessionTrackingRepository;
import de.fred4jupiter.fredbet.security.SecurityService;
import de.fred4jupiter.fredbet.service.config.RuntimeConfigurationService;
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
	private RuntimeConfigurationService runtimeConfigurationService;

	public List<AppUser> findAll() {
		return appUserRepository.findAll(new Sort(Direction.ASC, "username"));
	}

	public AppUser findByUserId(Long userId) {
		Optional<AppUser> appUser = appUserRepository.findById(userId);
		if (appUser.isPresent()) {
			return appUser.get();
		}

		throw new IllegalArgumentException("Given user with userId=" + userId + " does not exists.");
	}

	@CacheEvict(cacheNames = CacheNames.CHILD_RELATION, allEntries = true)
	public void createUser(AppUser appUser) throws UserAlreadyExistsException {
		AppUser foundUser = appUserRepository.findByUsername(appUser.getUsername());
		if (foundUser != null) {
			throw new UserAlreadyExistsException("User with username=" + appUser.getUsername() + " already exists.");
		}

		appUser.setPassword(passwordEncoder.encode(appUser.getPassword()));
		appUserRepository.save(appUser);
	}

	@CacheEvict(cacheNames = CacheNames.CHILD_RELATION, allEntries = true)
	public AppUser updateUser(Long userId, boolean passwordReset, boolean isChild) {
		return updateUser(userId, passwordReset, null, isChild);
	}

	@CacheEvict(cacheNames = CacheNames.CHILD_RELATION, allEntries = true)
	public AppUser updateUser(Long userId, boolean passwordReset, Set<String> roles, boolean isChild) {
		Assert.notNull(userId, "userId must be given");
		AppUser appUser = findByUserId(userId);
		if (roles != null && !roles.isEmpty()) {
			appUser.setRoles(roles);
		}

		if (passwordReset) {
			String passwordForReset = runtimeConfigurationService.loadRuntimeConfig().getPasswordForReset();
			appUser.setPassword(passwordEncoder.encode(passwordForReset));
		}

		appUser.setChild(isChild);
		appUserRepository.save(appUser);
		return appUser;
	}

	@CacheEvict(cacheNames = CacheNames.CHILD_RELATION, allEntries = true)
	public void deleteUser(Long userId) {
		AppUser appUser = findByUserId(userId);
		if (!appUser.isDeletable()) {
			throw new UserNotDeletableException("Could not delete user with name={}, because its marked as not deletable");
		}

		imageMetaDataRepository.deleteMetaDataByOwner(appUser.getId());

		appUserRepository.deleteById(userId);
	}

	public void changePassword(Long userId, String enteredOldPasswordPlain, String newPassword) {
		AppUser appUser = findByUserId(userId);

		final String oldSavedEncryptedPassword = appUser.getPassword();

		if (!passwordEncoder.matches(enteredOldPasswordPlain, oldSavedEncryptedPassword)) {
			throw new OldPasswordWrongException("The old password is wrong!");
		}

		appUser.setPassword(passwordEncoder.encode(newPassword));
		
		// reset firstLogin flag
		securityService.resetFirstLogin(appUser);
		
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

		AppUser foundUser = appUserRepository.findByUsername(newUsername);
		if (foundUser != null) {
			throw new UserAlreadyExistsException("User with username=" + newUsername + " already exists.");
		}

		AppUser userToBeRenamed = appUserRepository.findByUsername(oldUsername);
		if (userToBeRenamed == null) {
			LOG.error("User with username={} could not be found.", oldUsername);
			return;
		}

		userToBeRenamed.setUsername(newUsername);
		this.appUserRepository.save(userToBeRenamed);

		this.betRepository.renameUser(oldUsername, newUsername);
		this.extraBetRepository.renameUser(oldUsername, newUsername);
		this.sessionTrackingRepository.renameUser(oldUsername, newUsername);

		if (this.securityService.isUserLoggedIn()) {
			this.securityService.getCurrentUser().setUsername(newUsername);
		}
	}
}
