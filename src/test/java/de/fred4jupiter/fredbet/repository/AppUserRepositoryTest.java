package de.fred4jupiter.fredbet.repository;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

import de.fred4jupiter.fredbet.AbstractMongoEmbeddedTest;
import de.fred4jupiter.fredbet.FredBetRole;
import de.fred4jupiter.fredbet.domain.AppUser;

public class AppUserRepositoryTest extends AbstractMongoEmbeddedTest {

	@Autowired
	private AppUserRepository appUserRepository;

	@Test
	public void saveAppUser() {
		final String username = "robert";
		AppUser appUser = new AppUser(username, username, FredBetRole.ROLE_USER);
		appUser = appUserRepository.save(appUser);
		assertNotNull(appUser.getId());

		AppUser foundAppUser = appUserRepository.findOne(appUser.getId());
		assertNotNull(foundAppUser);
		assertEquals(appUser.getUsername(), foundAppUser.getUsername());
	}
}
