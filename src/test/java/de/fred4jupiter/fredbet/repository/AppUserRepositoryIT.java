package de.fred4jupiter.fredbet.repository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import de.fred4jupiter.fredbet.AbstractTransactionalIntegrationTest;
import de.fred4jupiter.fredbet.domain.AppUser;
import de.fred4jupiter.fredbet.domain.AppUserBuilder;
import de.fred4jupiter.fredbet.util.DateUtils;

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
	
	@Test
	public void fetchUsersWithLastLoginSortAsc() {
		appUserRepository.deleteAll();
		
		appUserRepository.save(AppUserBuilder.create().withDemoData().withUsernameAndPassword("robert", "robert").withLastLogin(DateUtils.toDate(LocalDateTime.now())).build());
		appUserRepository.save(AppUserBuilder.create().withDemoData().withUsernameAndPassword("albert", "albert").withLastLogin(DateUtils.toDate(LocalDateTime.now())).build());
		
		List<AppUser> resultList = appUserRepository.fetchLastLoginUsers();
		assertEquals(2, resultList.size());
		assertEquals("albert", resultList.get(0).getUsername());
		assertEquals("robert", resultList.get(1).getUsername());
	}
}
