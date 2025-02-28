package de.fred4jupiter.fredbet.user;

import de.fred4jupiter.fredbet.common.TransactionalIntegrationTest;
import de.fred4jupiter.fredbet.domain.AppUser;
import de.fred4jupiter.fredbet.domain.AppUserBuilder;
import de.fred4jupiter.fredbet.service.OldPasswordWrongException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Fail.fail;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TransactionalIntegrationTest
public class UserAdministrationServiceIT {

    @Autowired
    private UserAdministrationService userAdministrationService;

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    public void changePassword() {
        final String oldPassword = "blabla";
        final String newPassword = "mega";

        final AppUser appUser = AppUserBuilder.create().withDemoData().withPassword(oldPassword).build();
        userService.createUser(appUser);

        assertNotNull(appUser.getId());

        userAdministrationService.changePassword(appUser.getId(), oldPassword, newPassword);

        AppUser found = userService.findByUserId(appUser.getId());
        assertNotNull(found);
        assertTrue(passwordEncoder.matches(newPassword, found.getPassword()));
    }

    @Test
    public void changePasswordOldPasswordIsNotCorrect() {
        final String plainPassword = "hans";
        final String plainNewPassword = "mueller";
        final AppUser appUser = AppUserBuilder.create().withDemoData().withPassword(plainPassword).build();
        userService.createUser(appUser);

        assertNotNull(appUser.getId());

        try {
            userAdministrationService.changePassword(appUser.getId(), "wrongOldPassword", plainNewPassword);
            fail("OldPasswordWrongException should be thrown");
        } catch (OldPasswordWrongException e) {
            // OK
        }
    }
}
