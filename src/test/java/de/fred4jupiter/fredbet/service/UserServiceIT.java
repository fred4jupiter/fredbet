package de.fred4jupiter.fredbet.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import de.fred4jupiter.fredbet.AbstractTransactionalIntegrationTest;
import de.fred4jupiter.fredbet.domain.AppUser;
import de.fred4jupiter.fredbet.domain.AppUserBuilder;
import de.fred4jupiter.fredbet.security.FredBetRole;
import de.fred4jupiter.fredbet.web.profile.ChangePasswordCommand;

public class UserServiceIT extends AbstractTransactionalIntegrationTest {

	@Autowired
	private UserService userService;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Test
	public void avoidDuplicateUser() {
		AppUser appUser = AppUserBuilder.create().withUsernameAndPassword("michael", "michael").withRole(FredBetRole.ROLE_USER).build();

		userService.insertAppUser(appUser);

		try {
			userService.insertAppUser(appUser);
			fail("UserAlreadyExistsException should be thrown");
		} catch (UserAlreadyExistsException e) {
			// expected
		}
	}

	@Test
	public void changePassword() {
		final String oldPassword = "blabla";
		final String newPassword = "mega";

		final AppUser appUser = AppUserBuilder.create().withDemoData().withPassword(oldPassword).build();
		userService.insertAppUser(appUser);

		assertNotNull(appUser.getId());

		ChangePasswordCommand changePasswordCommand = new ChangePasswordCommand();
		changePasswordCommand.setOldPassword(oldPassword);
		changePasswordCommand.setNewPassword(newPassword);
		changePasswordCommand.setNewPasswordRepeat(newPassword);
		userService.changePassword(appUser.getId(), changePasswordCommand);

		AppUser found = userService.findByAppUserId(appUser.getId());
		assertNotNull(found);
		assertTrue(passwordEncoder.matches(newPassword, found.getPassword()));
	}

	@Test
	public void changePasswordOldPasswordIsNotCorrect() {
		final String plainPassword = "hans";
		final String plainNewPassword = "mueller";
		final AppUser appUser = AppUserBuilder.create().withDemoData().withPassword(plainPassword).build();
		userService.insertAppUser(appUser);

		assertNotNull(appUser.getId());

		ChangePasswordCommand changePasswordCommand = new ChangePasswordCommand();
		changePasswordCommand.setOldPassword("wrongOldPassword");
		changePasswordCommand.setNewPassword(plainNewPassword);
		changePasswordCommand.setNewPasswordRepeat(plainNewPassword);

		try {
			userService.changePassword(appUser.getId(), changePasswordCommand);
			fail("OldPasswordWrongException should be thrown");
		} catch (OldPasswordWrongException e) {
			// OK
		}
	}

	@Test
	public void updatePrivilegesAndNotPassword() {
		final AppUser appUser = AppUserBuilder.create().withDemoData().build();
		userService.insertAppUser(appUser);
		assertEquals(1, appUser.getRoles().size());

		// add role
		appUser.addRole(FredBetRole.ROLE_USER_ENTER_RESULTS);
		assertEquals(2, appUser.getRoles().size());
		userService.updateAppUser(appUser);

		AppUser foundAppUser = userService.findByAppUserId(appUser.getId());
		assertNotNull(foundAppUser);

		assertEquals(appUser.getUsername(), foundAppUser.getUsername());
		assertEquals(appUser.getPassword(), foundAppUser.getPassword());
	}
}
