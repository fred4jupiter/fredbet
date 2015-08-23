package de.fred4jupiter.fredbet.repository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import de.fred4jupiter.fredbet.AbstractMongoEmbeddedTest;
import de.fred4jupiter.fredbet.domain.Bet;

public class BetRepositoryTest extends AbstractMongoEmbeddedTest {

	private static final Logger LOG = LoggerFactory.getLogger(BetRepositoryTest.class);

	@Autowired
	private BetRepository betRepository;

	@Test
	public void calculateRanging() {
		createAndSaveBetForWith("michael", 3);
		createAndSaveBetForWith("michael", 2);

		createAndSaveBetForWith("bert", 2);
		createAndSaveBetForWith("bert", 1);

		List<UsernamePoints> ranking = betRepository.calculateRanging();
		ranking.forEach(usernamePoint -> LOG.debug("usernamePoint={}", usernamePoint));
		assertNotNull(ranking);
		assertEquals(2, ranking.size());

		UsernamePoints usernamePointsMichael = ranking.get(0);
		assertNotNull(usernamePointsMichael);
		assertEquals("michael", usernamePointsMichael.getUserName());
		assertEquals(Integer.valueOf(5), usernamePointsMichael.getTotalPoints());

		UsernamePoints usernamePointsBert = ranking.get(1);
		assertNotNull(usernamePointsBert);
		assertEquals("bert", usernamePointsBert.getUserName());
		assertEquals(Integer.valueOf(3), usernamePointsBert.getTotalPoints());
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
