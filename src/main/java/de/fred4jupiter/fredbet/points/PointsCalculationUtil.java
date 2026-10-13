package de.fred4jupiter.fredbet.points;

import de.fred4jupiter.fredbet.domain.entity.Bet;
import de.fred4jupiter.fredbet.domain.entity.Match;
import org.springframework.stereotype.Component;

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
        final PointsConfiguration pointsConfig = pointsConfigService.loadPointsConfig();
        return switch (BetOutcome.of(match, bet)) {
            case CORRECT_RESULT -> pointsConfig.getPointsCorrectResult();
            case SAME_GOAL_DIFFERENCE -> pointsConfig.getPointsSameGoalDifference();
            case CORRECT_WINNER -> pointsConfig.getPointsCorrectWinner();
            case CORRECT_NUMBER_OF_GOALS_ONE_TEAM -> pointsConfig.getPointsCorrectNumberOfGoalsOneTeam();
            case NONE -> 0;
        };
    }
}
