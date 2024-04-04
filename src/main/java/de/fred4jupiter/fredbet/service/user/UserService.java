package de.fred4jupiter.fredbet.service.user;

import de.fred4jupiter.fredbet.domain.*;
import de.fred4jupiter.fredbet.props.CacheNames;
import de.fred4jupiter.fredbet.props.FredbetProperties;
import de.fred4jupiter.fredbet.repository.*;
import de.fred4jupiter.fredbet.security.SecurityService;
import de.fred4jupiter.fredbet.service.BettingService;
import de.fred4jupiter.fredbet.service.OldPasswordWrongException;
import de.fred4jupiter.fredbet.service.RenameUsernameNotAllowedException;
import de.fred4jupiter.fredbet.service.config.RuntimeSettingsService;
import de.fred4jupiter.fredbet.util.Validator;
import de.fred4jupiter.fredbet.web.user.UserDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.*;

@Service
@Transactional
public class UserService {

    private static final Logger LOG = LoggerFactory.getLogger(UserService.class);

    private final AppUserRepository appUserRepository;

    private final PasswordEncoder passwordEncoder;

    private final SecurityService securityService;

    private final ImageMetaDataRepository imageMetaDataRepository;

    private final BetRepository betRepository;

    private final ExtraBetRepository extraBetRepository;

    private final SessionTrackingRepository sessionTrackingRepository;

    private final RuntimeSettingsService runtimeSettingsService;

    private final ImageGroupRepository imageGroupRepository;

    private final BettingService bettingService;

    private final ImageBinaryRepository imageBinaryRepository;

    private final String adminUsername;

    public UserService(AppUserRepository appUserRepository, PasswordEncoder passwordEncoder, SecurityService securityService,
                       ImageMetaDataRepository imageMetaDataRepository, BetRepository betRepository,
                       ExtraBetRepository extraBetRepository, SessionTrackingRepository sessionTrackingRepository,
                       RuntimeSettingsService runtimeSettingsService, ImageGroupRepository imageGroupRepository,
                       BettingService bettingService, ImageBinaryRepository imageBinaryRepository, FredbetProperties fredbetProperties) {
        this.appUserRepository = appUserRepository;
        this.passwordEncoder = passwordEncoder;
        this.securityService = securityService;
        this.imageMetaDataRepository = imageMetaDataRepository;
        this.betRepository = betRepository;
        this.extraBetRepository = extraBetRepository;
        this.sessionTrackingRepository = sessionTrackingRepository;
        this.runtimeSettingsService = runtimeSettingsService;
        this.imageGroupRepository = imageGroupRepository;
        this.bettingService = bettingService;
        this.imageBinaryRepository = imageBinaryRepository;
        this.adminUsername = fredbetProperties.adminUsername();
    }

    public List<AppUser> findAll() {
        return findAll(true);
    }

    public List<AppUser> findAll(boolean withDefaultAdminUser) {
        if (withDefaultAdminUser) {
            return appUserRepository.findAll(Sort.by(Direction.ASC, "username"));
        } else {
            return appUserRepository.findByUsernameNotLike(adminUsername, Sort.by(Direction.ASC, "username"));
        }
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
        LOG.info("creating user with username={}", appUser.getUsername());
        appUserRepository.save(appUser);
    }

    public boolean saveUserIfNotExists(AppUser appUser) {
        try {
            createUser(appUser);
            return true;
        } catch (UserAlreadyExistsException e) {
            LOG.debug(e.getMessage());
            return false;
        }
    }

    @CacheEvict(cacheNames = CacheNames.CHILD_RELATION, allEntries = true)
    public AppUser updateUser(Long userId, boolean isChild) {
        return updateUser(userId, null, isChild);
    }

    @CacheEvict(cacheNames = CacheNames.CHILD_RELATION, allEntries = true)
    public AppUser updateUser(Long userId, Set<String> roles, boolean isChild) {
        Assert.notNull(userId, "userId must be given");
        AppUser appUser = findByUserId(userId);
        if (Validator.isNotEmpty(roles)) {
            appUser.setRoles(roles);
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

        List<ImageMetaData> imageMetaDataList = imageMetaDataRepository.findByOwner(appUser);
        imageMetaDataList.forEach(imageMetaData -> {
            Optional<ImageBinary> imageOpt = imageBinaryRepository.findById(imageMetaData.getImageKey());
            imageOpt.ifPresent(imageBinary -> imageBinaryRepository.deleteById(imageBinary.getKey()));
        });
        imageMetaDataRepository.deleteAll(imageMetaDataList);

        appUserRepository.deleteById(userId);
    }

    @CacheEvict(cacheNames = CacheNames.CHILD_RELATION, allEntries = true)
    public void deleteAllUsers() {
        bettingService.deleteAllBets();

        List<AppUser> allUsers = appUserRepository.findAll();
        allUsers.forEach(appUser -> {
            if (!adminUsername.equals(appUser.getUsername())) {
                deleteUser(appUser.getId());
            }
        });
    }

    public String resetPasswordForUser(Long userId) {
        AppUser appUser = findByUserId(userId);
        String passwordForReset = runtimeSettingsService.loadRuntimeSettings().getPasswordForReset();
        appUser.setPassword(passwordEncoder.encode(passwordForReset));
        // user has to change his password when password is reset
        appUser.setFirstLogin(true);
        appUserRepository.save(appUser);
        return appUser.getUsername();
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

    public List<UserDto> findAllAsUserDto(boolean withDefaultAdminUser) {
        List<ImageMetaData> metaDataList = imageMetaDataRepository.loadImageMetaDataOfUserProfileImageSet();

        Map<String, ImageMetaData> map = toMap(metaDataList);

        return findAll(withDefaultAdminUser).stream()
                .map(appUser -> toUserDto(appUser, map))
                .sorted(Comparator.comparing(UserDto::getUsername, String.CASE_INSENSITIVE_ORDER))
                .toList();
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
        if (adminUsername.equals(oldUsername)) {
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

        renameUserProfileImageName(userToBeRenamed, newUsername);
    }

    private void renameUserProfileImageName(AppUser userToBeRenamed, String newUsername) {
        ImageGroup imageGroup = imageGroupRepository.findByUserProfileImageGroup();

        ImageMetaData imageMetaData = imageMetaDataRepository.findByOwnerAndImageGroup(userToBeRenamed, imageGroup);
        if (imageMetaData != null) {
            imageMetaData.setDescription(newUsername);
            imageMetaDataRepository.save(imageMetaData);
        }
    }

    public boolean createUserIfNotExists(String username, String password, boolean isChild, Set<String> roles) {
        AppUser appUser = appUserRepository.findByUsername(username);
        if (appUser != null) {
            LOG.warn("user with username={} already exists.", username);
            return false;
        }
        AppUser newAppUser = AppUserBuilder.create().withUsernameAndPassword(username, password)
                .withRoles(roles).withIsChild(isChild).build();
        appUserRepository.save(newAppUser);
        return true;
    }
}
