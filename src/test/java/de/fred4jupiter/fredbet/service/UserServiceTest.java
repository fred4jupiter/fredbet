package de.fred4jupiter.fredbet.service;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import de.fred4jupiter.fredbet.AbstractMongoEmbeddedTest;
import de.fred4jupiter.fredbet.FredBetRole;
import de.fred4jupiter.fredbet.domain.AppUser;

public class UserServiceTest extends AbstractMongoEmbeddedTest {

	@Autowired
	private UserService userService;

	@Test
	public void avoidDuplicateUser() {
		userService.save(new AppUser("michael", "michael", FredBetRole.ROLE_USER));
		userService.save(new AppUser("michael", "michael", FredBetRole.ROLE_USER));
	}
}
