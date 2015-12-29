package de.fred4jupiter.fredbet.repository;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasProperty;
import static org.junit.Assert.*;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import de.fred4jupiter.fredbet.AbstractMongoEmbeddedTest;
import de.fred4jupiter.fredbet.domain.Group;
import de.fred4jupiter.fredbet.domain.Match;
import de.fred4jupiter.fredbet.domain.MatchBuilder;
import de.fred4jupiter.fredbet.util.DateUtils;

public class MatchRepositoryIT extends AbstractMongoEmbeddedTest {

	@Autowired
	private MatchRepository matchRepository;

	@Test
	public void findAllOrderByKickOffDate() {
		createSomeMatches();

		List<Match> matchesOrderByKickOffDate = matchRepository.findAllByOrderByKickOffDateAsc();
		assertNotNull(matchesOrderByKickOffDate);
		assertFalse(matchesOrderByKickOffDate.isEmpty());

		assertThat(matchesOrderByKickOffDate, hasItem(hasProperty("teamOne", hasProperty("name", equalTo("Bulgarien")))));
		assertThat(matchesOrderByKickOffDate, hasItem(hasProperty("teamOne", hasProperty("name", equalTo("Belgien")))));
		assertThat(matchesOrderByKickOffDate, hasItem(hasProperty("teamOne", hasProperty("name", equalTo("Deutschland")))));
	}

	private void createSomeMatches() {
		matchRepository.save(MatchBuilder.create().withTeams("Deutschland", "Frankfreich").withGroup(Group.GROUP_B)
				.withStadium("Weserstadium, bremen").withKickOffDate(LocalDateTime.now().plusMinutes(20)).build());

		matchRepository.save(MatchBuilder.create().withTeams("Bulgarien", "Irland").withGroup(Group.GROUP_A)
				.withStadium("Westfalenstadium, Dortmund").withKickOffDate(LocalDateTime.now().plusMinutes(10)).build());

		matchRepository.save(MatchBuilder.create().withTeams("Belgien", "England").withGroup(Group.GROUP_D)
				.withStadium("AOL Arena, München").withKickOffDate(LocalDateTime.now().plusMinutes(15)).build());
	}

	@Test
	public void findByKickOffDateGreaterThanOrderByKickOffDateAsc() {
		matchRepository.deleteAll();
		
		createSomeMatches();

		List<Match> matches = matchRepository
				.findByKickOffDateGreaterThanOrderByKickOffDateAsc(DateUtils.toDate(LocalDateTime.now().plusMinutes(10)));
		assertNotNull(matches);
		assertEquals(2, matches.size());
	}
}
