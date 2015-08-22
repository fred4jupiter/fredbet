package de.fred4jupiter.fredbet.repository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import de.fred4jupiter.fredbet.AbstractMongoEmbeddedTest;
import de.fred4jupiter.fredbet.domain.Group;
import de.fred4jupiter.fredbet.domain.Match;
import de.fred4jupiter.fredbet.domain.MatchBuilder;

public class MatchRepositoryTest extends AbstractMongoEmbeddedTest {

	@Autowired
	private MatchRepository matchRepository;

	@Test
	public void findAllOrderByKickOffDate() {
		matchRepository.save(MatchBuilder.create().withTeams("Deutschland", "Frankfreich").withGroup(Group.GROUP_B)
				.withStadium("Weserstadium, bremen").withKickOffDate(LocalDateTime.now().plusMinutes(20)).build());

		matchRepository.save(MatchBuilder.create().withTeams("Bulgarien", "Irland").withGroup(Group.GROUP_A)
				.withStadium("Westfalenstadium, Dortmund").withKickOffDate(LocalDateTime.now().plusMinutes(10)).build());
		
		matchRepository.save(MatchBuilder.create().withTeams("Belgien", "England").withGroup(Group.GROUP_D)
				.withStadium("AOL Arena, München").withKickOffDate(LocalDateTime.now().plusMinutes(15)).build());

		List<Match> matchesOrderByKickOffDate = matchRepository.findAllByOrderByKickOffDateAsc();
		assertNotNull(matchesOrderByKickOffDate);
		assertFalse(matchesOrderByKickOffDate.isEmpty());

		assertEquals("Bulgarien", matchesOrderByKickOffDate.get(0).getTeamOne().getName());
		assertEquals("Belgien", matchesOrderByKickOffDate.get(1).getTeamOne().getName());
		assertEquals("Deutschland", matchesOrderByKickOffDate.get(2).getTeamOne().getName());
	}
}
