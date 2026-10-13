package de.fred4jupiter.fredbet.points;

import de.fred4jupiter.fredbet.common.UnitTest;
import de.fred4jupiter.fredbet.domain.Group;
import de.fred4jupiter.fredbet.domain.builder.BetBuilder;
import de.fred4jupiter.fredbet.domain.builder.MatchBuilder;
import de.fred4jupiter.fredbet.domain.entity.Bet;
import de.fred4jupiter.fredbet.domain.entity.Match;
import de.fred4jupiter.fredbet.team.TeamService;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.assertEquals;

@UnitTest
public class BetOutcomeUT {

    @Mock
    private TeamService teamService;

    @Test
    public void correctResult() {
        assertEquals(BetOutcome.CORRECT_RESULT, BetOutcome.of(createMatch(2, 1), createBet(2, 1)));
    }

    @Test
    public void sameGoalDifference() {
        assertEquals(BetOutcome.SAME_GOAL_DIFFERENCE, BetOutcome.of(createMatch(2, 1), createBet(3, 2)));
    }

    @Test
    public void sameGoalDifferenceOnDraw() {
        assertEquals(BetOutcome.SAME_GOAL_DIFFERENCE, BetOutcome.of(createMatch(1, 1), createBet(2, 2)));
    }

    @Test
    public void correctWinner() {
        assertEquals(BetOutcome.CORRECT_WINNER, BetOutcome.of(createMatch(3, 0), createBet(2, 1)));
    }

    @Test
    public void correctNumberOfGoalsOfOneTeam() {
        assertEquals(BetOutcome.CORRECT_NUMBER_OF_GOALS_ONE_TEAM, BetOutcome.of(createMatch(2, 1), createBet(2, 3)));
    }

    @Test
    public void none() {
        assertEquals(BetOutcome.NONE, BetOutcome.of(createMatch(4, 2), createBet(0, 1)));
    }

    private Bet createBet(Integer goalsTeamOne, Integer goalsTeamTwo) {
        return BetBuilder.create().withGoals(goalsTeamOne, goalsTeamTwo).build();
    }

    private Match createMatch(Integer goalsTeamOne, Integer goalsTeamTwo) {
        return MatchBuilder.create(teamService).withGroup(Group.GROUP_A).withGoals(goalsTeamOne, goalsTeamTwo).build();
    }
}
