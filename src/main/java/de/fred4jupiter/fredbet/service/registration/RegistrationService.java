package de.fred4jupiter.fredbet.service.registration;

import de.fred4jupiter.fredbet.domain.AppUserBuilder;
import de.fred4jupiter.fredbet.security.FredBetUserGroup;
import de.fred4jupiter.fredbet.service.config.RuntimeSettingsService;
import de.fred4jupiter.fredbet.service.user.UserService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class RegistrationService {

    private static final Logger LOG = LoggerFactory.getLogger(RegistrationService.class);

    private final UserService userService;

    private final RuntimeSettingsService runtimeSettingsService;

    public RegistrationService(UserService userService, RuntimeSettingsService runtimeSettingsService) {
        this.userService = userService;
        this.runtimeSettingsService = runtimeSettingsService;
    }

    public boolean isTokenValid(String token) {
        if (StringUtils.isBlank(token)) {
            return false;
        }
        final String registrationCode = runtimeSettingsService.loadRuntimeSettings().getRegistrationCode();
        if (StringUtils.isBlank(registrationCode)) {
            LOG.warn("No registration code set!");
            return false;
        }

        return registrationCode.trim().equals(token.trim());
    }

    public boolean isSelfRegistrationEnabled() {
        return runtimeSettingsService.loadRuntimeSettings().isSelfRegistrationEnabled();
    }

    public void registerNewUser(String username, String newPassword, boolean child) {
        AppUserBuilder builder = AppUserBuilder.create()
                .withUsernameAndPassword(username, newPassword)
                .withUserGroup(FredBetUserGroup.ROLE_USER)
                .withIsChild(child);

        userService.createUser(builder.build());
    }
}
