package de.fred4jupiter.fredbet.repository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import de.fred4jupiter.fredbet.AbstractTransactionalIntegrationTest;
import de.fred4jupiter.fredbet.domain.AppUser;
import de.fred4jupiter.fredbet.domain.AppUserBuilder;

public class AppUserRepositoryIT extends AbstractTransactionalIntegrationTest {

	@Autowired
	private AppUserRepository appUserRepository;

	@Test
	public void saveAppUser() {
		AppUser appUser = AppUserBuilder.create().withDemoData().build();
		appUser = appUserRepository.save(appUser);
		assertNotNull(appUser.getId());

		AppUser foundAppUser = appUserRepository.findOne(appUser.getId());
		assertNotNull(foundAppUser);
		assertEquals(appUser.getUsername(), foundAppUser.getUsername());
	}
}
