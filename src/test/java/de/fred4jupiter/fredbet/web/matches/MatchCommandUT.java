package de.fred4jupiter.fredbet.web.matches;

import de.fred4jupiter.fredbet.team.TeamService;
import de.fred4jupiter.fredbet.common.UnitTest;
import de.fred4jupiter.fredbet.domain.Group;
import de.fred4jupiter.fredbet.domain.builder.BetBuilder;
import de.fred4jupiter.fredbet.domain.builder.MatchBuilder;
import de.fred4jupiter.fredbet.domain.entity.Bet;
import de.fred4jupiter.fredbet.domain.entity.Match;
import de.fred4jupiter.fredbet.points.BetOutcome;
import de.fred4jupiter.fredbet.props.FredbetConstants;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@UnitTest
public class MatchCommandUT {

    @Mock
    private TeamService teamService;

    @Test
    public void teamResultNoResultReturnsDefaultCssClass() {
        Match match = MatchBuilder.create(teamService).build();

        MatchCommand matchCommand = new MatchCommand(match, null);

        assertEquals("label-default", matchCommand.getTeamResultOneCssClasses());
        assertEquals("label-default", matchCommand.getTeamResultTwoCssClasses());
    }

    @Test
    public void teamResultWithResultReturnsDefaultCssClass() {
        Match match = MatchBuilder.create(teamService).withGoals(1, 2).withGroup(Group.GROUP_A).build();
        MatchCommand matchCommand = new MatchCommand(match, null);

        assertEquals("label-info", matchCommand.getTeamResultOneCssClasses());
        assertEquals("label-info", matchCommand.getTeamResultTwoCssClasses());
    }

    @Test
    public void teamResultWithResultReturnsInfoCssClassPenaltyWinnerOne() {
        Match match = MatchBuilder.create(teamService)
            .withGoals(1, 1)
            .withGroup(Group.ROUND_OF_SIXTEEN)
            .withPenaltyWinnerOne(true)
            .build();
        MatchCommand matchCommand = new MatchCommand(match, null);

        assertEquals("label-info" + " " + FredbetConstants.BADGE_PENALTY_WINNER_MATCH_CSS_CLASS, matchCommand.getTeamResultOneCssClasses());
        assertEquals("label-info", matchCommand.getTeamResultTwoCssClasses());
    }

    @Test
    public void teamResultWithResultReturnsInfoCssClassPenaltyWinnerTwo() {
        Match match = MatchBuilder.create(teamService)
            .withGoals(1, 1)
            .withGroup(Group.ROUND_OF_SIXTEEN)
            .withPenaltyWinnerOne(false)
            .build();
        MatchCommand matchCommand = new MatchCommand(match, null);

        assertEquals("label-info", matchCommand.getTeamResultOneCssClasses());
        assertEquals("label-info" + " " + FredbetConstants.BADGE_PENALTY_WINNER_MATCH_CSS_CLASS, matchCommand.getTeamResultTwoCssClasses());
    }

    @Test
    public void noPenaltyTeamInGroupMatches() {
        Match match = MatchBuilder.create(teamService)
            .withGoals(1, 1)
            .withGroup(Group.GROUP_B)
            .withPenaltyWinnerOne(false)
            .build();
        MatchCommand matchCommand = new MatchCommand(match, null);

        assertEquals("label-info", matchCommand.getTeamResultOneCssClasses());
        assertEquals("label-info", matchCommand.getTeamResultTwoCssClasses());
    }

    @Test
    public void userBetsNoResultReturnsDefaultCssClass() {
        MatchCommand matchCommand = new MatchCommand(null, Optional.ofNullable(null));

        assertEquals("label-default", matchCommand.getUserBetGoalsTeamOneCssClasses());
        assertEquals("label-default", matchCommand.getUserBetGoalsTeamTwoCssClasses());
    }

    @Test
    public void userBetsWithResultReturnsDefaultCssClass() {
        Match match = MatchBuilder.create(teamService)
            .withGoals(1, 2)
            .withGroup(Group.GROUP_A)
            .build();

        Bet bet = BetBuilder.create()
            .withGoals(1, 2)
            .build();

        MatchCommand matchCommand = new MatchCommand(match, Optional.of(bet));

        assertEquals("label-success", matchCommand.getUserBetGoalsTeamOneCssClasses());
        assertEquals("label-success", matchCommand.getUserBetGoalsTeamTwoCssClasses());
    }

    @Test
    public void userBetsWithResultReturnsSuccessCssClassPenaltyWinnerOne() {
        Match match = MatchBuilder.create(teamService)
            .withGoals(1, 1)
            .withGroup(Group.ROUND_OF_SIXTEEN)
            .withPenaltyWinnerOne(true)
            .build();

        Bet bet = BetBuilder.create()
            .withGoals(1, 1)
            .withPenaltyWinnerOne(true)
            .build();

        MatchCommand matchCommand = new MatchCommand(match, Optional.of(bet));

        assertEquals("label-success" + " " + FredbetConstants.BADGE_PENALTY_WINNER_BET_CSS_CLASS, matchCommand.getUserBetGoalsTeamOneCssClasses());
        assertEquals("label-success", matchCommand.getUserBetGoalsTeamTwoCssClasses());
    }

    @Test
    public void userBetsWithResultReturnsSuccessCssClassPenaltyWinnerTwo() {
        Match match = MatchBuilder.create(teamService)
            .withGoals(1, 1)
            .withGroup(Group.ROUND_OF_SIXTEEN)
            .withPenaltyWinnerOne(false)
            .build();

        Bet bet = BetBuilder.create()
            .withGoals(1, 1)
            .withPenaltyWinnerOne(false)
            .build();

        MatchCommand matchCommand = new MatchCommand(match, Optional.of(bet));

        assertEquals("label-success", matchCommand.getUserBetGoalsTeamOneCssClasses());
        assertEquals("label-success" + " " + FredbetConstants.BADGE_PENALTY_WINNER_BET_CSS_CLASS, matchCommand.getUserBetGoalsTeamTwoCssClasses());
    }

    @Test
    public void pointsPendingAsLongAsMatchHasNoResult() {
        Match match = MatchBuilder.create(teamService).withGroup(Group.GROUP_A).build();
        Bet bet = BetBuilder.create().withGoals(1, 2).build();

        MatchCommand matchCommand = new MatchCommand(match, Optional.of(bet));

        assertNull(matchCommand.getBetOutcome());
        assertEquals("points-pending", matchCommand.getPointsCssClass());
        assertEquals("matches.list.points.pending", matchCommand.getPointsMsgKey());
    }

    @Test
    public void pointsOutcomeOfFinishedMatch() {
        Match match = MatchBuilder.create(teamService).withGoals(2, 1).withGroup(Group.GROUP_A).build();
        Bet bet = BetBuilder.create().withGoals(3, 2).withJoker(true).withPoints(4).build();

        MatchCommand matchCommand = new MatchCommand(match, Optional.of(bet));

        assertEquals(BetOutcome.SAME_GOAL_DIFFERENCE, matchCommand.getBetOutcome());
        assertEquals("points-same-goal-difference", matchCommand.getPointsCssClass());
        assertEquals("points.config.pointsSameGoalDifference", matchCommand.getPointsMsgKey());
        assertTrue(matchCommand.isJokerBet());
    }

    @Test
    public void noPointsWithoutBetOnFinishedMatch() {
        Match match = MatchBuilder.create(teamService).withGoals(2, 1).withGroup(Group.GROUP_A).build();

        MatchCommand matchCommand = new MatchCommand(match, Optional.empty());

        assertEquals(BetOutcome.NONE, matchCommand.getBetOutcome());
        assertEquals("points-none", matchCommand.getPointsCssClass());
        assertFalse(matchCommand.isJokerBet());
    }

    @Test
    public void outcomeWithoutPointsIsShownAsNoPoints() {
        Match match = MatchBuilder.create(teamService).withGoals(2, 1).withGroup(Group.GROUP_A).build();
        Bet bet = BetBuilder.create().withGoals(2, 3).withPoints(0).build();

        MatchCommand matchCommand = new MatchCommand(match, Optional.of(bet));

        assertEquals(BetOutcome.CORRECT_NUMBER_OF_GOALS_ONE_TEAM, matchCommand.getBetOutcome());
        assertEquals("points-none", matchCommand.getPointsCssClass());
        assertEquals("matches.list.points.none", matchCommand.getPointsMsgKey());
    }
}
