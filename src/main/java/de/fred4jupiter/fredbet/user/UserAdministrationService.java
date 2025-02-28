package de.fred4jupiter.fredbet.user;

import de.fred4jupiter.fredbet.domain.AppUser;
import de.fred4jupiter.fredbet.repository.AppUserRepository;
import de.fred4jupiter.fredbet.security.SecurityService;
import de.fred4jupiter.fredbet.service.OldPasswordWrongException;
import de.fred4jupiter.fredbet.service.config.RuntimeSettingsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserAdministrationService {

    private final RuntimeSettingsService runtimeSettingsService;

    private final PasswordEncoder passwordEncoder;

    private final SecurityService securityService;

    private final AppUserRepository appUserRepository;

    public UserAdministrationService(RuntimeSettingsService runtimeSettingsService, PasswordEncoder passwordEncoder,
                                     SecurityService securityService, AppUserRepository appUserRepository) {
        this.runtimeSettingsService = runtimeSettingsService;
        this.passwordEncoder = passwordEncoder;
        this.securityService = securityService;
        this.appUserRepository = appUserRepository;
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
}
