package de.fred4jupiter.fredbet.service;

import static org.junit.Assert.*;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;

import de.fred4jupiter.fredbet.AbstractMongoEmbeddedTest;
import de.fred4jupiter.fredbet.FredBetRole;
import de.fred4jupiter.fredbet.domain.AppUser;

public class UserServiceTest extends AbstractMongoEmbeddedTest {

	@Autowired
	private UserService userService;

	@Test
	public void avoidDuplicateUser() {
		try {
			userService.save(new AppUser("michael", "michael", FredBetRole.USER));
			userService.save(new AppUser("michael", "michael", FredBetRole.USER));
			fail("DuplicateKeyException should be thrown");
		} catch (DuplicateKeyException e) {
			// expected
		}
	}
}
