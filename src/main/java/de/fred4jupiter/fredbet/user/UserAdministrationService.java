package de.fred4jupiter.fredbet.user;

import de.fred4jupiter.fredbet.domain.entity.AppUser;
import de.fred4jupiter.fredbet.domain.entity.ImageGroup;
import de.fred4jupiter.fredbet.domain.entity.ImageMetaData;
import de.fred4jupiter.fredbet.props.FredbetProperties;
import de.fred4jupiter.fredbet.repository.*;
import de.fred4jupiter.fredbet.security.SecurityService;
import de.fred4jupiter.fredbet.settings.RuntimeSettingsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserAdministrationService {

    private static final Logger LOG = LoggerFactory.getLogger(UserAdministrationService.class);

    private final RuntimeSettingsService runtimeSettingsService;

    private final PasswordEncoder passwordEncoder;

    private final SecurityService securityService;

    private final AppUserRepository appUserRepository;

    private final BetRepository betRepository;

    private final ExtraBetRepository extraBetRepository;

    private final SessionTrackingRepository sessionTrackingRepository;

    private final ImageGroupRepository imageGroupRepository;

    private final ImageMetaDataRepository imageMetaDataRepository;

    private final FredbetProperties fredbetProperties;

    public UserAdministrationService(RuntimeSettingsService runtimeSettingsService, PasswordEncoder passwordEncoder,
                                     SecurityService securityService, AppUserRepository appUserRepository,
                                     BetRepository betRepository, ExtraBetRepository extraBetRepository,
                                     SessionTrackingRepository sessionTrackingRepository,
                                     ImageGroupRepository imageGroupRepository, ImageMetaDataRepository imageMetaDataRepository,
                                     FredbetProperties fredbetProperties) {
        this.runtimeSettingsService = runtimeSettingsService;
        this.passwordEncoder = passwordEncoder;
        this.securityService = securityService;
        this.appUserRepository = appUserRepository;
        this.betRepository = betRepository;
        this.extraBetRepository = extraBetRepository;
        this.sessionTrackingRepository = sessionTrackingRepository;
        this.imageGroupRepository = imageGroupRepository;
        this.imageMetaDataRepository = imageMetaDataRepository;
        this.fredbetProperties = fredbetProperties;
    }

    public String resetPasswordForUser(Long userId) {
        AppUser appUser = appUserRepository.findByUserId(userId);
        String passwordForReset = runtimeSettingsService.loadRuntimeSettings().getPasswordForReset();
        appUser.setPassword(passwordEncoder.encode(passwordForReset));
        // user has to change his password when password is reset
        appUser.setFirstLogin(true);
        appUserRepository.save(appUser);
        return appUser.getUsername();
    }

    public void changePassword(Long userId, String enteredOldPasswordPlain, String newPassword) {
        AppUser appUser = appUserRepository.findByUserId(userId);

        final String oldSavedEncryptedPassword = appUser.getPassword();

        if (!passwordEncoder.matches(enteredOldPasswordPlain, oldSavedEncryptedPassword)) {
            throw new OldPasswordWrongException("The old password is wrong!");
        }

        appUser.setPassword(passwordEncoder.encode(newPassword));

        // reset firstLogin flag
        securityService.resetFirstLogin(appUser);

        appUserRepository.save(appUser);
    }

    public void renameUser(String oldUsername, String newUsername) {
        if (fredbetProperties.adminUsername().equals(oldUsername)) {
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
}
