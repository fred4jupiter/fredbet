package de.fred4jupiter.fredbet.service;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import de.fred4jupiter.fredbet.domain.Bet;
import de.fred4jupiter.fredbet.domain.Match;

@RunWith(MockitoJUnitRunner.class)
public class PointsCalculationServiceTest {

	@InjectMocks
	private PointsCalculationService pointsCalculationService;

	@Test
	public void threePointsOnExactResult() {
		Match match = createMatch(2, 1);
		Bet bet = createBet(2, 1);

		assertEquals(Integer.valueOf(3), pointsCalculationService.calculatePointsFor(match, bet));
	}

	@Test
	public void twoPointsOnSameGoalDifference() {
		Match match = createMatch(2, 1);
		Bet bet = createBet(3, 2);

		assertEquals(Integer.valueOf(2), pointsCalculationService.calculatePointsFor(match, bet));
	}
	
	@Test
	public void twoPointsOnSameGoalDifferenceOtherIsWinner() {
		Match match = createMatch(2, 1);
		Bet bet = createBet(2, 3);

		assertEquals(Integer.valueOf(2), pointsCalculationService.calculatePointsFor(match, bet));
	}

	@Test
	public void onePointOnWinner() {
		Match match = createMatch(6, 3);
		Bet bet = createBet(2, 1);

		assertEquals(Integer.valueOf(1), pointsCalculationService.calculatePointsFor(match, bet));
	}
	
	@Test
	public void wrongResultNoPoints() {
		Match match = createMatch(6, 3);
		Bet bet = createBet(3, 4);

		assertEquals(Integer.valueOf(0), pointsCalculationService.calculatePointsFor(match, bet));
	}

	private Bet createBet(Integer goalsTeamOne, Integer goalsTeamTwo) {
		Bet bet = new Bet();
		bet.setGoalsTeamOne(goalsTeamOne);
		bet.setGoalsTeamTwo(goalsTeamTwo);
		return bet;
	}

	private Match createMatch(Integer goalsTeamOne, Integer goalsTeamTwo) {
		Match match = new Match("Deutschland", "Italien");
		match.enterResult(goalsTeamOne, goalsTeamTwo);
		return match;
	}
}
