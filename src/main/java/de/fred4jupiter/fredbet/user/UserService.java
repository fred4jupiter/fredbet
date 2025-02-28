package de.fred4jupiter.fredbet.user;

import de.fred4jupiter.fredbet.domain.AppUser;
import de.fred4jupiter.fredbet.domain.ImageBinary;
import de.fred4jupiter.fredbet.domain.ImageMetaData;
import de.fred4jupiter.fredbet.props.CacheNames;
import de.fred4jupiter.fredbet.props.FredbetProperties;
import de.fred4jupiter.fredbet.repository.AppUserRepository;
import de.fred4jupiter.fredbet.repository.ImageBinaryRepository;
import de.fred4jupiter.fredbet.repository.ImageMetaDataRepository;
import de.fred4jupiter.fredbet.security.SecurityService;
import de.fred4jupiter.fredbet.betting.BettingService;
import de.fred4jupiter.fredbet.image.ImageAdministrationService;
import de.fred4jupiter.fredbet.util.Validator;
import de.fred4jupiter.fredbet.web.user.UserDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.io.IOException;
import java.util.*;

@Service
@Transactional
public class UserService {

    private static final Logger LOG = LoggerFactory.getLogger(UserService.class);

    private final AppUserRepository appUserRepository;

    private final PasswordEncoder passwordEncoder;

    private final SecurityService securityService;

    private final ImageMetaDataRepository imageMetaDataRepository;

    private final BettingService bettingService;

    private final ImageBinaryRepository imageBinaryRepository;

    private final FredbetProperties fredbetProperties;

    private final ImageAdministrationService imageAdministrationService;

    private final Resource defaultUserProfileImage;

    public UserService(AppUserRepository appUserRepository, PasswordEncoder passwordEncoder, SecurityService securityService,
                       ImageMetaDataRepository imageMetaDataRepository,
                       BettingService bettingService, ImageBinaryRepository imageBinaryRepository,
                       FredbetProperties fredbetProperties, ImageAdministrationService imageAdministrationService,
                       @Value("classpath:/content/default_profile_image.jpg") Resource defaultUserProfileImage) {
        this.appUserRepository = appUserRepository;
        this.passwordEncoder = passwordEncoder;
        this.securityService = securityService;
        this.imageMetaDataRepository = imageMetaDataRepository;
        this.bettingService = bettingService;
        this.imageBinaryRepository = imageBinaryRepository;
        this.fredbetProperties = fredbetProperties;
        this.imageAdministrationService = imageAdministrationService;
        this.defaultUserProfileImage = defaultUserProfileImage;
    }

    public List<AppUser> findAll() {
        return findAll(true);
    }

    public List<AppUser> findAll(boolean withDefaultAdminUser) {
        if (withDefaultAdminUser) {
            return appUserRepository.findAll(Sort.by(Direction.ASC, "username"));
        } else {
            return appUserRepository.findByUsernameNotLike(fredbetProperties.adminUsername(), Sort.by(Direction.ASC, "username"));
        }
    }

    @CacheEvict(cacheNames = CacheNames.CHILD_RELATION, allEntries = true)
    public AppUser createUser(AppUser appUser) throws UserAlreadyExistsException {
        AppUser foundUser = appUserRepository.findByUsername(appUser.getUsername());
        if (foundUser != null) {
            throw new UserAlreadyExistsException("User with username=" + appUser.getUsername() + " already exists.");
        }

        appUser.setPassword(passwordEncoder.encode(appUser.getPassword()));
        LOG.info("creating user with username={}", appUser.getUsername());
        AppUser savedAppUser = appUserRepository.save(appUser);
        imageAdministrationService.saveUserProfileImage(getDefaultUserProfileImage(), savedAppUser);
        return savedAppUser;
    }

    @CacheEvict(cacheNames = CacheNames.CHILD_RELATION, allEntries = true)
    public AppUser createUserIfNotExists(AppUser appUser) {
        AppUser foundUser = appUserRepository.findByUsername(appUser.getUsername());
        if (foundUser != null) {
            return foundUser;
        }

        return createUser(appUser);
    }

    private byte[] getDefaultUserProfileImage() {
        try {
            return this.defaultUserProfileImage.getContentAsByteArray();
        } catch (IOException e) {
            throw new IllegalStateException("Default user profile image could not be loaded. Cause: " + e.getMessage());
        }
    }

    @CacheEvict(cacheNames = CacheNames.CHILD_RELATION, allEntries = true)
    public AppUser updateUser(Long userId, boolean isChild) {
        return updateUser(userId, null, isChild);
    }

    @CacheEvict(cacheNames = CacheNames.CHILD_RELATION, allEntries = true)
    public AppUser updateUser(Long userId, Set<String> roles, boolean isChild) {
        Assert.notNull(userId, "userId must be given");
        AppUser appUser = appUserRepository.findByUserId(userId);
        if (Validator.isNotEmpty(roles)) {
            appUser.setRoles(roles);
        }

        appUser.setChild(isChild);
        appUserRepository.save(appUser);
        return appUser;
    }

    @CacheEvict(cacheNames = CacheNames.CHILD_RELATION, allEntries = true)
    public void deleteUser(Long userId) {
        AppUser appUser = appUserRepository.findByUserId(userId);
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
            if (!fredbetProperties.adminUsername().equals(appUser.getUsername())) {
                deleteUser(appUser.getId());
            }
        });
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

    public void saveUserProfileImage(byte[] binary) {
        final AppUser appUser = appUserRepository.findByUserId(securityService.getCurrentUser().getId());
        ImageMetaData imageMetaData = securityService.getCurrentUserProfileImageMetaData();
        imageAdministrationService.saveUserProfileImage(binary, appUser, imageMetaData);
    }

    public AppUser findByUserId(Long userId) {
        return appUserRepository.findByUserId(userId);
    }
}
