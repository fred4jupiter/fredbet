package de.fred4jupiter.fredbet.web.matches;

import de.fred4jupiter.fredbet.UnitTest;
import de.fred4jupiter.fredbet.domain.Group;
import de.fred4jupiter.fredbet.props.FredbetConstants;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;

import static org.junit.jupiter.api.Assertions.assertEquals;

@UnitTest
public class MatchCommandUT {

    @InjectMocks
    private MatchCommand matchCommand;

    @Test
    public void teamResultNoResultReturnsDefaultCssClass() {
        assertEquals("label-default", matchCommand.getTeamResultOneCssClasses());
        assertEquals("label-default", matchCommand.getTeamResultTwoCssClasses());
    }

    @Test
    public void teamResultWithResultReturnsDefaultCssClass() {
        matchCommand.setGroup(Group.GROUP_A);
        matchCommand.setTeamResultOne(1);
        matchCommand.setTeamResultTwo(2);

        assertEquals("label-info", matchCommand.getTeamResultOneCssClasses());
        assertEquals("label-info", matchCommand.getTeamResultTwoCssClasses());
    }

    @Test
    public void teamResultWithResultReturnsInfoCssClassPenaltyWinnerOne() {
        matchCommand.setGroup(Group.ROUND_OF_SIXTEEN);
        matchCommand.setTeamResultOne(1);
        matchCommand.setTeamResultTwo(1);
        matchCommand.setPenaltyWinnerOneMatch(true);

        assertEquals("label-info" + " " + FredbetConstants.BADGE_PENALTY_WINNER_MATCH_CSS_CLASS, matchCommand.getTeamResultOneCssClasses());
        assertEquals("label-info", matchCommand.getTeamResultTwoCssClasses());
    }

    @Test
    public void teamResultWithResultReturnsInfoCssClassPenaltyWinnerTwo() {
        matchCommand.setGroup(Group.ROUND_OF_SIXTEEN);
        matchCommand.setTeamResultOne(1);
        matchCommand.setTeamResultTwo(1);
        matchCommand.setPenaltyWinnerOneMatch(false);

        assertEquals("label-info", matchCommand.getTeamResultOneCssClasses());
        assertEquals("label-info" + " " + FredbetConstants.BADGE_PENALTY_WINNER_MATCH_CSS_CLASS, matchCommand.getTeamResultTwoCssClasses());
    }

    @Test
    public void noPenaltyTeamInGroupMatches() {
        matchCommand.setGroup(Group.GROUP_B);
        matchCommand.setTeamResultOne(1);
        matchCommand.setTeamResultTwo(1);
        matchCommand.setPenaltyWinnerOneMatch(false);

        assertEquals("label-info", matchCommand.getTeamResultOneCssClasses());
        assertEquals("label-info", matchCommand.getTeamResultTwoCssClasses());
    }

    @Test
    public void userBetsNoResultReturnsDefaultCssClass() {
        assertEquals("label-default", matchCommand.getUserBetGoalsTeamOneCssClasses());
        assertEquals("label-default", matchCommand.getUserBetGoalsTeamTwoCssClasses());
    }

    @Test
    public void userBetsWithResultReturnsDefaultCssClass() {
        matchCommand.setGroup(Group.GROUP_A);
        matchCommand.setUserBetGoalsTeamOne(1);
        matchCommand.setUserBetGoalsTeamTwo(2);

        assertEquals("label-success", matchCommand.getUserBetGoalsTeamOneCssClasses());
        assertEquals("label-success", matchCommand.getUserBetGoalsTeamTwoCssClasses());
    }

    @Test
    public void userBetsWithResultReturnsSuccessCssClassPenaltyWinnerOne() {
        matchCommand.setGroup(Group.ROUND_OF_SIXTEEN);
        matchCommand.setUserBetGoalsTeamOne(1);
        matchCommand.setUserBetGoalsTeamTwo(1);
        matchCommand.setPenaltyWinnerOneBet(true);

        assertEquals("label-success" + " " + FredbetConstants.BADGE_PENALTY_WINNER_BET_CSS_CLASS, matchCommand.getUserBetGoalsTeamOneCssClasses());
        assertEquals("label-success", matchCommand.getUserBetGoalsTeamTwoCssClasses());
    }

    @Test
    public void userBetsWithResultReturnsSuccessCssClassPenaltyWinnerTwo() {
        matchCommand.setGroup(Group.ROUND_OF_SIXTEEN);
        matchCommand.setUserBetGoalsTeamOne(1);
        matchCommand.setUserBetGoalsTeamTwo(1);
        matchCommand.setPenaltyWinnerOneBet(false);

        assertEquals("label-success", matchCommand.getUserBetGoalsTeamOneCssClasses());
        assertEquals("label-success" + " " + FredbetConstants.BADGE_PENALTY_WINNER_BET_CSS_CLASS, matchCommand.getUserBetGoalsTeamTwoCssClasses());
    }
}
