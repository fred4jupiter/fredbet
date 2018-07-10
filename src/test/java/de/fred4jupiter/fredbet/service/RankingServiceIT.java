package de.fred4jupiter.fredbet.service;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import java.util.List;

import de.fred4jupiter.fredbet.service.ranking.RankingService;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import de.fred4jupiter.fredbet.AbstractTransactionalIntegrationTest;
import de.fred4jupiter.fredbet.data.DataBasePopulator;
import de.fred4jupiter.fredbet.domain.AppUser;
import de.fred4jupiter.fredbet.domain.AppUserBuilder;
import de.fred4jupiter.fredbet.domain.RankingSelection;
import de.fred4jupiter.fredbet.props.FredbetConstants;
import de.fred4jupiter.fredbet.repository.UsernamePoints;

public class RankingServiceIT extends AbstractTransactionalIntegrationTest {

	private static final Logger LOG = LoggerFactory.getLogger(RankingServiceIT.class);

	@Autowired
	private DataBasePopulator dataBasePopulator;

	@Autowired
	private RankingService rankingService;

	@Autowired
	private UserService userService;

	@Test
	public void getRankingForUsersButWithoutTechnicalUser() {
		saveIfNotPresent(AppUserBuilder.create().withDemoData().build());
		saveIfNotPresent(AppUserBuilder.create().withDemoData().build());
		saveIfNotPresent(AppUserBuilder.create().withDemoData().build());
		saveIfNotPresent(AppUserBuilder.create().withDemoData().build());

		final String username = FredbetConstants.TECHNICAL_USERNAME;
		saveIfNotPresent(AppUserBuilder.create().withDemoData().withUsernameAndPassword(username, "test").build());

		dataBasePopulator.createRandomMatches();
		dataBasePopulator.createDemoBetsForAllUsers();
		dataBasePopulator.createDemoResultsForAllMatches();

		List<UsernamePoints> rankings = rankingService.calculateCurrentRanking(RankingSelection.MIXED);
		assertNotNull(rankings);
		assertFalse(rankings.isEmpty());
		assertThat(rankings, not(hasItem(hasProperty("userName", equalTo(username)))));
	}

	@Test
	public void getRankingForAdultUsers() {
		saveIfNotPresent(AppUserBuilder.create().withDemoData().withUsernameAndPassword("fred", "fred").withIsChild(true).build());
		saveIfNotPresent(AppUserBuilder.create().withDemoData().withUsernameAndPassword("holger", "holger").withIsChild(false).build());

		dataBasePopulator.createRandomMatches();
		dataBasePopulator.createDemoBetsForAllUsers();
		dataBasePopulator.createDemoResultsForAllMatches();

		List<UsernamePoints> rankings = rankingService.calculateCurrentRanking(RankingSelection.ONLY_ADULTS);
		assertNotNull(rankings);
		assertFalse(rankings.isEmpty());
		assertThat(rankings, not(hasItem(hasProperty("userName", equalTo("fred")))));
		assertThat(rankings, hasItem(hasProperty("userName", equalTo("holger"))));
	}
	
	@Test
	public void getRankingForChildUsers() {
		saveIfNotPresent(AppUserBuilder.create().withDemoData().withUsernameAndPassword("fred", "fred").withIsChild(true).build());
		saveIfNotPresent(AppUserBuilder.create().withDemoData().withUsernameAndPassword("holger", "holger").withIsChild(false).build());

		dataBasePopulator.createRandomMatches();
		dataBasePopulator.createDemoBetsForAllUsers();
		dataBasePopulator.createDemoResultsForAllMatches();

		List<UsernamePoints> rankings = rankingService.calculateCurrentRanking(RankingSelection.ONLY_CHILDREN);
		assertNotNull(rankings);
		assertFalse(rankings.isEmpty());
		assertThat(rankings, not(hasItem(hasProperty("userName", equalTo("holger")))));
		assertThat(rankings, hasItem(hasProperty("userName", equalTo("fred"))));
	}

	private void saveIfNotPresent(AppUser appUser) {
		try {
			userService.createUser(appUser);
		} catch (UserAlreadyExistsException e) {
			LOG.debug(e.getMessage());
		}
	}
}
