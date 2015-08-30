package de.fred4jupiter.fredbet.service;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import de.fred4jupiter.fredbet.domain.Bet;
import de.fred4jupiter.fredbet.domain.Match;
import de.fred4jupiter.fredbet.domain.MatchBuilder;

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
	public void twoPointsOnSameGoalDifferenceHigherRange() {
		Match match = createMatch(10, 2);
		Bet bet = createBet(20, 12);

		assertEquals(Integer.valueOf(2), pointsCalculationService.calculatePointsFor(match, bet));
	}
	
	@Test
	public void noPointsOnSameGoalDifferenceOtherIsWinner() {
		Match match = createMatch(4, 2);
		Bet bet = createBet(2, 4);

		assertEquals(Integer.valueOf(0), pointsCalculationService.calculatePointsFor(match, bet));
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
	
	@Test
	public void correctGoalDifferenceButWrongRelation() {
		Match match = createMatch(4, 3);
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
		return MatchBuilder.create().withTeams("Deutschland", "Italien").withGoals(goalsTeamOne, goalsTeamTwo).build();
	}
}
