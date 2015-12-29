package de.fred4jupiter.fredbet.repository;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import de.fred4jupiter.fredbet.AbstractMongoEmbeddedTest;
import de.fred4jupiter.fredbet.domain.Bet;
import de.fred4jupiter.fredbet.domain.Match;
import de.fred4jupiter.fredbet.domain.MatchBuilder;

public class BetRepositoryIT extends AbstractMongoEmbeddedTest {

	private static final Logger LOG = LoggerFactory.getLogger(BetRepositoryIT.class);

	@Autowired
	private BetRepository betRepository;
	
	@Autowired
	private MatchRepository matchRepository;
	
	@Before
	public void setup() {
		betRepository.deleteAll();
		matchRepository.deleteAll();
	}

	@Test
	public void calculateRanging() {
		final String userName1 = "james";
		createAndSaveBetForWith(userName1, 3);
		createAndSaveBetForWith(userName1, 2);

		final String userName2 = "roberto";
		createAndSaveBetForWith(userName2, 2);
		createAndSaveBetForWith(userName2, 1);

		List<UsernamePoints> ranking = betRepository.calculateRanging();
		ranking.forEach(usernamePoint -> LOG.debug("usernamePoint={}", usernamePoint));
		assertNotNull(ranking);
		assertEquals(2, ranking.size());

		UsernamePoints usernamePointsMichael = ranking.get(0);
		assertNotNull(usernamePointsMichael);
		assertEquals(userName1, usernamePointsMichael.getUserName());
		assertEquals(Integer.valueOf(5), usernamePointsMichael.getTotalPoints());

		UsernamePoints usernamePointsBert = ranking.get(1);
		assertNotNull(usernamePointsBert);
		assertEquals(userName2, usernamePointsBert.getUserName());
		assertEquals(Integer.valueOf(3), usernamePointsBert.getTotalPoints());
	}

	@Test
	public void countNumberOfBetsForMatch() {
		Bet bet = new Bet();
		bet.setGoalsTeamOne(2);
		bet.setGoalsTeamTwo(1);
		bet.setUserName("michael");
		bet.setPoints(1);

		Match match = MatchBuilder.create().build();
		bet.setMatch(match);
		matchRepository.save(match);

		Bet savedBet = betRepository.save(bet);
		assertNotNull(savedBet);
		assertNotNull(savedBet.getId());

		Long count = betRepository.countByMatch(match);
		assertThat(count, equalTo(1L));
	}

	private void createAndSaveBetForWith(String userName, Integer points) {
		Bet bet = new Bet();
		bet.setGoalsTeamOne(2);
		bet.setGoalsTeamTwo(1);
		bet.setUserName(userName);
		bet.setPoints(points);
		betRepository.save(bet);
	}
}
