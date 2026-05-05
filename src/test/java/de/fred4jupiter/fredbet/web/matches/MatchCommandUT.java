package de.fred4jupiter.fredbet.web.matches;

import de.fred4jupiter.fredbet.TeamService;
import de.fred4jupiter.fredbet.common.UnitTest;
import de.fred4jupiter.fredbet.domain.Group;
import de.fred4jupiter.fredbet.domain.builder.BetBuilder;
import de.fred4jupiter.fredbet.domain.builder.MatchBuilder;
import de.fred4jupiter.fredbet.domain.entity.Bet;
import de.fred4jupiter.fredbet.domain.entity.Match;
import de.fred4jupiter.fredbet.props.FredbetConstants;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
}
