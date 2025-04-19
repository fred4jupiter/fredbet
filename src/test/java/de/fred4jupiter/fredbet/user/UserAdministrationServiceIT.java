package de.fred4jupiter.fredbet.user;

import de.fred4jupiter.fredbet.common.TransactionalIntegrationTest;
import de.fred4jupiter.fredbet.domain.entity.AppUser;
import de.fred4jupiter.fredbet.domain.builder.AppUserBuilder;
import de.fred4jupiter.fredbet.domain.entity.Bet;
import de.fred4jupiter.fredbet.betting.repository.BetRepository;
import de.fred4jupiter.fredbet.teambundle.TeamBundle;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Fail.fail;
import static org.junit.jupiter.api.Assertions.*;

@TransactionalIntegrationTest
public class UserAdministrationServiceIT {

    @Autowired
    private UserAdministrationService userAdministrationService;

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private BeanFactory beanFactory;

    @Autowired
    private BetRepository betRepository;

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

    @Test
    public void renameUser() {
        FredBetUsageBuilder fredBetUsageBuilder = beanFactory.getBean(FredBetUsageBuilder.class);

        AppUser appUser = fredBetUsageBuilder.withAppUser().withMatch(TeamBundle.WORLD_CUP).withBet().build();

        final String oldUserName = appUser.getUsername();

        final String newUsername = "Klarky";

        userAdministrationService.renameUser(oldUserName, newUsername);

        AppUser foundUser = userService.findByUserId(appUser.getId());
        assertNotNull(foundUser);
        assertEquals(newUsername, foundUser.getUsername());

        List<Bet> betsByOldName = this.betRepository.findByUserName(oldUserName);
        assertThat(betsByOldName.size()).isEqualTo(0);

        List<Bet> betsByNewName = this.betRepository.findByUserName(newUsername);
        assertNotNull(betsByNewName);
        assertThat(betsByNewName.size()).isGreaterThan(0);
    }
}
