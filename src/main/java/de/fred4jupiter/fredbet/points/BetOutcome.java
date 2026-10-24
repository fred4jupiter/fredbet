package de.fred4jupiter.fredbet.points;

import de.fred4jupiter.fredbet.domain.entity.Bet;
import de.fred4jupiter.fredbet.domain.entity.Match;
import org.springframework.util.Assert;

/**
 * Category of a bet compared to the match result. Each category is worth the points configured in {@link PointsConfiguration}.
 * The order of the checks matters: a bet is assigned to the best matching category.
 */
public enum BetOutcome {

    CORRECT_RESULT("points.config.pointsCorrectResult"),

    SAME_GOAL_DIFFERENCE("points.config.pointsSameGoalDifference"),

    CORRECT_WINNER("points.config.pointsCorrectWinner"),

    CORRECT_NUMBER_OF_GOALS_ONE_TEAM("points.config.pointsCorrectNumberOfGoalsOneTeam"),

    NONE("matches.list.points.none");

    private final String msgKey;

    BetOutcome(String msgKey) {
        this.msgKey = msgKey;
    }

    public String getMsgKey() {
        return msgKey;
    }

    public static BetOutcome of(Match match, Bet bet) {
        Assert.notNull(match.getGoalsTeamOne(), "no goals team one given");
        Assert.notNull(match.getGoalsTeamTwo(), "no goals team two given");

        if (isSameGoalResult(match, bet)) {
            return CORRECT_RESULT;
        }
        if (isSameGoalDifference(match, bet)) {
            return SAME_GOAL_DIFFERENCE;
        }
        if (isCorrectWinner(match, bet)) {
            return CORRECT_WINNER;
        }
        if (isCorrectNumberOfGoalsOfOneTeam(match, bet)) {
            return CORRECT_NUMBER_OF_GOALS_ONE_TEAM;
        }
        return NONE;
    }

    private static boolean isCorrectWinner(Match match, Bet bet) {
        if (match.isKnockoutMatch() && match.isUndecidedResult()) {
            // you can only get points if the penalty winner is correct and this is calculated separately
            return false;
        }
        return (match.isTeamOneWinner() && bet.isTeamOneWinner()) || (match.isTeamTwoWinner() && bet.isTeamTwoWinner());
    }

    private static boolean isSameGoalDifference(Match match, Bet bet) {
        if (match.isTeamOneWinner() && bet.isTeamTwoWinner()) {
            return false;
        }
        if (match.isTeamTwoWinner() && bet.isTeamOneWinner()) {
            return false;
        }

        return match.getGoalDifference().intValue() == bet.getGoalDifference().intValue();
    }

    private static boolean isSameGoalResult(Match match, Bet bet) {
        return match.getGoalsTeamOne().equals(bet.getGoalsTeamOne()) && match.getGoalsTeamTwo().equals(bet.getGoalsTeamTwo());
    }

    private static boolean isCorrectNumberOfGoalsOfOneTeam(Match match, Bet bet) {
        return match.getGoalsTeamOne().equals(bet.getGoalsTeamOne()) || match.getGoalsTeamTwo().equals(bet.getGoalsTeamTwo());
    }
}
