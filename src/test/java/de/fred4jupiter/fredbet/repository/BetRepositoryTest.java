package de.fred4jupiter.fredbet.repository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import de.fred4jupiter.fredbet.AbstractMongoEmbeddedTest;
import de.fred4jupiter.fredbet.domain.Bet;

public class BetRepositoryTest extends AbstractMongoEmbeddedTest {

	@Autowired
	private BetRepository betRepository;

	@Test
	public void calculateRanging() {
		Bet bet = new Bet();
		bet.setGoalsTeamOne(2);
		bet.setGoalsTeamTwo(1);
		bet.setUserName("michael");
		bet.setPoints(3);
		betRepository.save(bet);
		
		Bet bet2 = new Bet();
		bet2.setGoalsTeamOne(3);
		bet2.setGoalsTeamTwo(1);
		bet2.setUserName("michael");
		bet2.setPoints(1);
		betRepository.save(bet2);

		Bet bet3 = new Bet();
		bet3.setGoalsTeamOne(3);
		bet3.setGoalsTeamTwo(1);
		bet3.setUserName("bert");
		bet3.setPoints(1);
		betRepository.save(bet3);
		
		Bet bet4 = new Bet();
		bet4.setGoalsTeamOne(2);
		bet4.setGoalsTeamTwo(5);
		bet4.setUserName("bert");
		bet4.setPoints(0);
		betRepository.save(bet4);

		List<UsernamePoints> ranking = betRepository.calculateRanging();
		assertNotNull(ranking);
		assertEquals(2, ranking.size());

		UsernamePoints usernamePointsMichael = ranking.get(0);
		assertNotNull(usernamePointsMichael);
		assertEquals("michael", usernamePointsMichael.getUserName());
		assertEquals(Integer.valueOf(4), usernamePointsMichael.getTotalPoints());

		UsernamePoints usernamePointsBert = ranking.get(1);
		assertNotNull(usernamePointsBert);
		assertEquals("bert", usernamePointsBert.getUserName());
		assertEquals(Integer.valueOf(1), usernamePointsBert.getTotalPoints());
	}
}
