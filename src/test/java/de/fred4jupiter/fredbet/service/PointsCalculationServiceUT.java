package de.fred4jupiter.fredbet.service;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

import de.fred4jupiter.fredbet.domain.Bet;
import de.fred4jupiter.fredbet.domain.Group;
import de.fred4jupiter.fredbet.domain.Match;
import de.fred4jupiter.fredbet.domain.MatchBuilder;

@RunWith(MockitoJUnitRunner.class)
public class PointsCalculationServiceUT {

	@InjectMocks
	private PointsCalculationService pointsCalculationService;

	@Test
	public void threePointsOnExactResult() {
		Match match = createMatch(2, 1);
		Bet bet = createBet(2, 1);

		assertEquals(3, pointsCalculationService.calculatePointsFor(match, bet));
	}

	@Test
	public void twoPointsOnSameGoalDifference() {
		Match match = createMatch(2, 1);
		Bet bet = createBet(3, 2);

		assertEquals(2, pointsCalculationService.calculatePointsFor(match, bet));
	}

	@Test
	public void twoPointsOnSameGoalDifferenceHigherRange() {
		Match match = createMatch(10, 2);
		Bet bet = createBet(20, 12);

		assertEquals(2, pointsCalculationService.calculatePointsFor(match, bet));
	}

	@Test
	public void noPointsOnSameGoalDifferenceOtherIsWinner() {
		Match match = createMatch(4, 2);
		Bet bet = createBet(2, 4);

		assertEquals(0, pointsCalculationService.calculatePointsFor(match, bet));
	}

	@Test
	public void onePointOnWinner() {
		Match match = createMatch(6, 3);
		Bet bet = createBet(2, 1);

		assertEquals(1, pointsCalculationService.calculatePointsFor(match, bet));
	}

	@Test
	public void wrongResultNoPoints() {
		Match match = createMatch(6, 3);
		Bet bet = createBet(3, 4);

		assertEquals(0, pointsCalculationService.calculatePointsFor(match, bet));
	}

	@Test
	public void correctGoalDifferenceButWrongRelation() {
		Match match = createMatch(4, 3);
		Bet bet = createBet(3, 4);

		assertEquals(0, pointsCalculationService.calculatePointsFor(match, bet));
	}

	@Test
	public void extraPointOnCorrectPenaltyWinner() {
		Match match = MatchBuilder.create().withGroup(Group.ROUND_OF_SIXTEEN).withTeams("Deutschland", "Italien").withGoals(4, 4).build();
		match.setPenaltyWinnerOne(true);

		Bet bet = createBet(4, 4);
		bet.setPenaltyWinnerOne(true);

		assertEquals(4, pointsCalculationService.calculatePointsFor(match, bet));
	}
	
	@Test
	public void noPointsInFinalIfSetOfWin() {
		Match match = MatchBuilder.create().withGroup(Group.FINAL).withTeams("Deutschland", "Italien").withGoals(5, 5).build();
		match.setPenaltyWinnerOne(false);

		Bet bet = createBet(4, 3);
		bet.setPenaltyWinnerOne(true);

		assertEquals(0, pointsCalculationService.calculatePointsFor(match, bet));
	}

	@Test
	public void extraPointOnCorrectPenaltyOtherWinner() {
		Match match = MatchBuilder.create().withGroup(Group.ROUND_OF_SIXTEEN).withTeams("Deutschland", "Italien").withGoals(0, 0).build();
		match.setPenaltyWinnerOne(false);
		Bet bet = createBet(0, 0);
		bet.setPenaltyWinnerOne(false);

		assertEquals(4, pointsCalculationService.calculatePointsFor(match, bet));
	}

	@Test
	public void pointsWithJokerDoublesPoints() {
		Match match = MatchBuilder.create().withGroup(Group.GROUP_B).withTeams("Deutschland", "Italien").withGoals(0, 0).build();
		Bet bet = createBet(0, 0);
		bet.setPenaltyWinnerOne(false);
		bet.setJoker(true);

		assertEquals(6, pointsCalculationService.calculatePointsFor(match, bet));
	}
	
	@Test
	public void pointsWithJokerInPenaltyDoublesPoints() {
		Match match = MatchBuilder.create().withGroup(Group.ROUND_OF_SIXTEEN).withTeams("Deutschland", "Italien").withGoals(1, 1).build();
		match.setPenaltyWinnerOne(false);
		Bet bet = createBet(1, 1);
		bet.setPenaltyWinnerOne(false);
		bet.setJoker(true);

		assertEquals(8, pointsCalculationService.calculatePointsFor(match, bet));
	}
	
	private Bet createBet(Integer goalsTeamOne, Integer goalsTeamTwo) {
		Bet bet = new Bet();
		bet.setGoalsTeamOne(goalsTeamOne);
		bet.setGoalsTeamTwo(goalsTeamTwo);
		return bet;
	}

	private Match createMatch(Integer goalsTeamOne, Integer goalsTeamTwo) {
		return MatchBuilder.create().withGroup(Group.GROUP_A).withTeams("Deutschland", "Italien").withGoals(goalsTeamOne, goalsTeamTwo)
				.build();
	}
}
