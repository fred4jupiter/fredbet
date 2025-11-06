package de.fred4jupiter.fredbet.user;

import de.fred4jupiter.fredbet.domain.UserSetting;
import de.fred4jupiter.fredbet.domain.entity.AppUser;
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

    public void updateUserSetting(AppUser appUser, UserSetting userSetting) {
        Optional<AppUser> appUserOpt = appUserRepository.findById(appUser.getId());
        appUserOpt.ifPresent(foundAppUser -> {
            final String theme = userSetting.toTheme();
            foundAppUser.getAppUserSetting().setTheme(theme);
            appUserRepository.save(foundAppUser);
            appUser.getAppUserSetting().setTheme(theme);
            LOG.info("updated user settings for user: {}", foundAppUser.getUsername());
        });
    }
}
