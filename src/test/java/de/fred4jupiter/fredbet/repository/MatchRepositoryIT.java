package de.fred4jupiter.fredbet.repository;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasProperty;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import de.fred4jupiter.fredbet.AbstractTransactionalIntegrationTest;
import de.fred4jupiter.fredbet.domain.BetBuilder;
import de.fred4jupiter.fredbet.domain.Country;
import de.fred4jupiter.fredbet.domain.Group;
import de.fred4jupiter.fredbet.domain.Match;
import de.fred4jupiter.fredbet.domain.MatchBuilder;

public class MatchRepositoryIT extends AbstractTransactionalIntegrationTest {

	@Autowired
	private MatchRepository matchRepository;

	@Autowired
	private BetRepository betRepository;

	@Test
	public void findAllOrderByKickOffDate() {
		createSomeMatches();

		List<Match> matchesOrderByKickOffDate = matchRepository.findAllByOrderByKickOffDateAsc();
		assertNotNull(matchesOrderByKickOffDate);
		assertFalse(matchesOrderByKickOffDate.isEmpty());

		assertThat(matchesOrderByKickOffDate, hasItem(hasProperty("teamNameOne", equalTo("Bulgarien"))));
		assertThat(matchesOrderByKickOffDate, hasItem(hasProperty("teamNameOne", equalTo("Belgien"))));
		assertThat(matchesOrderByKickOffDate, hasItem(hasProperty("teamNameOne", equalTo("Deutschland"))));
	}

	private void createSomeMatches() {
		matchRepository.save(MatchBuilder.create().withTeams("Deutschland", "Frankfreich").withGroup(Group.GROUP_B)
				.withStadium("Weserstadium, bremen").withKickOffDate(LocalDateTime.now().plusMinutes(20)).withGoals(1, 2).build());

		matchRepository.save(MatchBuilder.create().withTeams("Bulgarien", "Irland").withGroup(Group.GROUP_A)
				.withStadium("Westfalenstadium, Dortmund").withKickOffDate(LocalDateTime.now().plusMinutes(10)).withGoals(1, 2).build());

		matchRepository.save(MatchBuilder.create().withTeams("Belgien", "England").withGroup(Group.GROUP_D)
				.withStadium("AOL Arena, München").withKickOffDate(LocalDateTime.now().plusMinutes(15)).withGoals(1, 2).build());

		matchRepository.save(MatchBuilder.create().withTeams(Country.AFGHANISTAN, Country.ARMENIA).withGroup(Group.QUARTER_FINAL)
				.withStadium("AOL Arena, München").withKickOffDate(LocalDateTime.now().plusMinutes(90)).withGoals(1, 2).build());

		matchRepository.save(MatchBuilder.create().withTeams(Country.BANGLADESH, Country.COLOMBIA).withGroup(Group.FINAL)
				.withStadium("AOL Arena, München").withKickOffDate(LocalDateTime.now().minusDays(8)).withGoals(1, 2).build());
	}

	@Test
	public void findByKickOffDateGreaterThanOrderByKickOffDateAsc() {
		matchRepository.deleteAll();

		createSomeMatches();

		LocalDateTime groupKickOffBeginSelectionDate = LocalDateTime.now().minusMinutes(10);
		LocalDateTime koKickOffBeginSelectionDate = LocalDateTime.now().minusMinutes(90);

		List<Match> matches = matchRepository.findUpcomingMatches(groupKickOffBeginSelectionDate, koKickOffBeginSelectionDate);
		assertNotNull(matches);
		assertEquals(4, matches.size());
	}

	@Test
	public void findMatchesOfJokerBets() {
		matchRepository.deleteAll();
		
		Match match1 = MatchBuilder.create().withTeams("Deutschland", "Frankfreich").withGroup(Group.GROUP_B)
				.withStadium("Weserstadium, bremen").withKickOffDate(LocalDateTime.now().plusMinutes(20)).withGoals(1, 2).build();
		matchRepository.save(match1);

		Match match2 = MatchBuilder.create().withTeams("Bulgarien", "Irland").withGroup(Group.GROUP_A)
				.withStadium("Westfalenstadium, Dortmund").withKickOffDate(LocalDateTime.now().plusMinutes(10)).withGoals(1, 2).build();
		matchRepository.save(match2);

		final String userName = "Albert";
		betRepository.save(BetBuilder.create().withGoals(1, 2).withJoker(true).withMatch(match1).withUsername(userName).build());

		betRepository.save(BetBuilder.create().withGoals(2, 6).withJoker(true).withMatch(match2).withUsername("Karl").build());

		List<Match> matches = matchRepository.findMatchesOfJokerBetsForUser(userName);
		assertNotNull(matches);

		assertEquals(1, matches.size());
	}
}
