package de.fred4jupiter.fredbet.points;

import de.fred4jupiter.fredbet.domain.entity.Bet;
import de.fred4jupiter.fredbet.domain.entity.Match;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component
 class PointsCalculationUtil {

    private static final int JOKER_MULTIPLIER = 2;

    private final PointsConfigService pointsConfigService;

    public PointsCalculationUtil(PointsConfigService pointsConfigService) {
        this.pointsConfigService = pointsConfigService;
    }

    public int calculatePointsFor(Match match, Bet bet) {
        final int standardPoints = calculateStandardPointsFor(match, bet);
        final int penaltyPoints = calculatePenaltyPointsFor(match, bet);

        final int subtotal = standardPoints + penaltyPoints;
        if (bet.isJoker()) {
            return subtotal * JOKER_MULTIPLIER;
        }

        return subtotal;
    }

    private int calculatePenaltyPointsFor(Match match, Bet bet) {
        if (match.isGroupMatch()) {
            return 0;
        }

        if (match.isUndecidedResult() && bet.isUndecidedBetting()) {
            if (match.isPenaltyWinnerOne() && bet.isPenaltyWinnerOne()) {
                return 1;
            }

            if (!match.isPenaltyWinnerOne() && !bet.isPenaltyWinnerOne()) {
                return 1;
            }
        }

        return 0;
    }

    private int calculateStandardPointsFor(Match match, Bet bet) {
        if (isSameGoalResult(match, bet)) {
            return pointsConfigService.loadPointsConfig().getPointsCorrectResult();
        }

        if (isSameGoalDifference(match, bet)) {
            return pointsConfigService.loadPointsConfig().getPointsSameGoalDifference();
        }

        if (isCorrectWinner(match, bet)) {
            return pointsConfigService.loadPointsConfig().getPointsCorrectWinner();
        }

        if (isCorrectNumberOfGoalsOfOneTeam(match, bet)) {
            return pointsConfigService.loadPointsConfig().getPointsCorrectNumberOfGoalsOneTeam();
        }
        return 0;
    }


    private boolean isCorrectWinner(Match match, Bet bet) {
        if (!match.isGroupMatch() && match.isUndecidedResult()) {
            // you can only get points if the penalty winner is correct and this is calculated in the other method
            return false;
        }
        return (match.isTeamOneWinner() && bet.isTeamOneWinner()) || (match.isTeamTwoWinner() && bet.isTeamTwoWinner());
    }

    private boolean isSameGoalDifference(Match match, Bet bet) {
        if (match.isTeamOneWinner() && bet.isTeamTwoWinner()) {
            return false;
        }
        if (match.isTeamTwoWinner() && bet.isTeamOneWinner()) {
            return false;
        }

        return match.getGoalDifference().intValue() == bet.getGoalDifference().intValue();
    }

    private boolean isSameGoalResult(Match match, Bet bet) {
        Assert.notNull(match.getGoalsTeamOne(), "no goals team one given");
        Assert.notNull(match.getGoalsTeamTwo(), "no goals team two given");
        return match.getGoalsTeamOne().equals(bet.getGoalsTeamOne()) && match.getGoalsTeamTwo().equals(bet.getGoalsTeamTwo());
    }

    private boolean isCorrectNumberOfGoalsOfOneTeam(Match match, Bet bet) {
        Assert.notNull(match.getGoalsTeamOne(), "no goals team one given");
        Assert.notNull(match.getGoalsTeamTwo(), "no goals team two given");
        return match.getGoalsTeamOne().equals(bet.getGoalsTeamOne()) || match.getGoalsTeamTwo().equals(bet.getGoalsTeamTwo());
    }
}
