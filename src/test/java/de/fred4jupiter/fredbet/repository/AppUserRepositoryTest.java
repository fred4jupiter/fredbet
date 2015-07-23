package de.fred4jupiter.fredbet.repository;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

import de.fred4jupiter.fredbet.AbstractMongoEmbeddedTest;
import de.fred4jupiter.fredbet.domain.AppUser;

public class AppUserRepositoryTest extends AbstractMongoEmbeddedTest{

	@Autowired
	private AppUserRepository appUserRepository;
	
	@Test
	public void saveAppUser() {
		AppUser appUser = new AppUser("michael", "michael", "ROLE_USER");
		appUser = appUserRepository.save(appUser);
		assertNotNull(appUser.getId());
		
		AppUser foundAppUser = appUserRepository.findOne(appUser.getId());
		assertNotNull(foundAppUser);
		assertEquals(appUser.getUsername(), foundAppUser.getUsername());
	}
}
