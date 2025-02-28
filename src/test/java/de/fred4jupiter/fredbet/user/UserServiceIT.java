package de.fred4jupiter.fredbet.user;

import de.fred4jupiter.fredbet.common.TransactionalIntegrationTest;
import de.fred4jupiter.fredbet.domain.AppUser;
import de.fred4jupiter.fredbet.domain.AppUserBuilder;
import de.fred4jupiter.fredbet.security.FredBetUserGroup;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Fail.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@TransactionalIntegrationTest
public class UserServiceIT {

    @Autowired
    private UserService userService;

    @Test
    public void avoidDuplicateUser() {
        AppUser appUser = AppUserBuilder.create().withUsernameAndPassword("mustermann", "mustermann").withUserGroup(FredBetUserGroup.ROLE_USER)
            .build();

        userService.createUser(appUser);

        try {
            userService.createUser(appUser);
            fail("UserAlreadyExistsException should be thrown");
        } catch (UserAlreadyExistsException e) {
            // expected
        }
    }

    @Test
    public void updatePrivilegesAndNotPassword() {
        final AppUser appUser = AppUserBuilder.create().withDemoData().build();
        userService.createUser(appUser);
        assertEquals(1, appUser.getRoles().size());

        // add role
        appUser.addUserGroup(FredBetUserGroup.ROLE_USER_ENTER_RESULTS);
        assertEquals(2, appUser.getRoles().size());
        userService.updateUser(appUser.getId(), appUser.getRoles(), false);

        AppUser foundAppUser = userService.findByUserId(appUser.getId());
        assertNotNull(foundAppUser);

        assertEquals(appUser.getUsername(), foundAppUser.getUsername());
        assertEquals(appUser.getPassword(), foundAppUser.getPassword());
    }


    @Test
    public void deleteAllUsers() {
        final AppUser appUser1 = AppUserBuilder.create().withDemoData().build();
        userService.createUser(appUser1);
        final AppUser appUser2 = AppUserBuilder.create().withDemoData().build();
        userService.createUser(appUser2);

        userService.deleteAllUsers();
    }
}
