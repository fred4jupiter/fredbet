package de.fred4jupiter.fredbet.service;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

import de.fred4jupiter.fredbet.AbstractMongoEmbeddedTest;
import de.fred4jupiter.fredbet.FredBetRole;
import de.fred4jupiter.fredbet.domain.AppUser;
import de.fred4jupiter.fredbet.web.user.ChangePasswordCommand;

public class UserServiceTest extends AbstractMongoEmbeddedTest {

	@Autowired
	private UserService userService;

	@Test
	public void avoidDuplicateUser() {
		userService.save(new AppUser("michael", "michael", FredBetRole.ROLE_USER));
		userService.save(new AppUser("michael", "michael", FredBetRole.ROLE_USER));
	}

	@Test
	public void changePassword() {
		final String plainPassword = "feuerstein";
		final String plainNewPassword = "wilma";
		AppUser appUser = new AppUser("fred", plainPassword, FredBetRole.ROLE_USER);
		userService.save(appUser);

		assertNotNull(appUser.getId());

		ChangePasswordCommand changePasswordCommand = new ChangePasswordCommand();
		changePasswordCommand.setOldPassword(plainPassword);
		changePasswordCommand.setNewPassword(plainNewPassword);
		userService.changePassword(appUser, changePasswordCommand);
	}

	@Test
	public void changePasswordOldPasswordIsNotCorrect() {
		final String plainPassword = "hans";
		final String plainNewPassword = "mueller";
		AppUser appUser = new AppUser("mini", plainPassword, FredBetRole.ROLE_USER);
		userService.save(appUser);

		assertNotNull(appUser.getId());

		ChangePasswordCommand changePasswordCommand = new ChangePasswordCommand();
		changePasswordCommand.setOldPassword("wrongOldPassword");
		changePasswordCommand.setNewPassword(plainNewPassword);

		try {
			userService.changePassword(appUser, changePasswordCommand);
			fail("OldPasswordWrongException should be thrown");
		} catch (OldPasswordWrongException e) {
			// OK
		}

	}
}
