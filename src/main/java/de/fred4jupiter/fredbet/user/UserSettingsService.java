package de.fred4jupiter.fredbet.user;

import de.fred4jupiter.fredbet.domain.UserSetting;
import de.fred4jupiter.fredbet.domain.entity.AppUser;
import de.fred4jupiter.fredbet.domain.entity.AppUserSetting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class UserSettingsService {

    private static final Logger LOG = LoggerFactory.getLogger(UserSettingsService.class);

    private final AppUserRepository appUserRepository;

    public UserSettingsService(AppUserRepository appUserRepository) {
        this.appUserRepository = appUserRepository;
    }

    public void updateUserSetting(AppUser currentUser, UserSetting userSetting) {
        Optional<AppUser> appUserOpt = appUserRepository.findById(currentUser.getId());
        appUserOpt.ifPresent(foundAppUser -> {
            mapPropertiesFor(foundAppUser.getAppUserSetting(), userSetting);
            appUserRepository.save(foundAppUser);
            LOG.info("updated user settings for saved user: {}", foundAppUser.getUsername());
        });

        mapPropertiesFor(currentUser.getAppUserSetting(), userSetting);
        LOG.info("updated user settings for current user: {}", currentUser.getUsername());
    }

    private void mapPropertiesFor(AppUserSetting appUserSetting, UserSetting userSetting) {
        appUserSetting.setTheme(userSetting.toTheme());
        appUserSetting.setBootswatchTheme(userSetting.bootswatchTheme());
        appUserSetting.setNavbarLayout(userSetting.navbarLayout());
    }

}
