package de.fred4jupiter.fredbet.service.registration;

import de.fred4jupiter.fredbet.domain.AppUser;
import de.fred4jupiter.fredbet.domain.AppUserBuilder;
import de.fred4jupiter.fredbet.security.FredBetUserGroup;
import de.fred4jupiter.fredbet.service.config.RuntimeConfigurationService;
import de.fred4jupiter.fredbet.service.user.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Service
public class RegistrationService {

    private final UserService userService;

    private final RuntimeConfigurationService runtimeConfigurationService;

    public RegistrationService(UserService userService, RuntimeConfigurationService runtimeConfigurationService) {
        this.userService = userService;
        this.runtimeConfigurationService = runtimeConfigurationService;
    }

    public boolean isTokenValid(String token) {
        String registrationCode = runtimeConfigurationService.loadRuntimeConfig().getRegistrationCode();
        if (StringUtils.isNotBlank(registrationCode) && !registrationCode.equals(token)) {
            return false;
        }

        return true;
    }

    public boolean isSelfRegistrationEnabled() {
        return runtimeConfigurationService.loadRuntimeConfig().isSelfRegistrationEnabled();
    }

    public void registerNewUser(String username, String newPassword) {
        AppUser appUser = AppUserBuilder.create()
                .withUsernameAndPassword(username, newPassword)
                .withUserGroup(FredBetUserGroup.ROLE_USER).build();
        userService.createUser(appUser);
    }
}
