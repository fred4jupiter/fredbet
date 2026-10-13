package de.fred4jupiter.fredbet.web.matches;

import de.fred4jupiter.fredbet.domain.entity.Bet;
import de.fred4jupiter.fredbet.domain.entity.Match;
import de.fred4jupiter.fredbet.points.BetOutcome;

/**
 * Points of a bet for a match, as shown in the badge of fragments/points_badge.html.
 */
public record PointsBadge(String cssClass, String msgKey, String text, boolean jokerHint) {

    private static final PointsBadge PENDING = new PointsBadge("points-pending", "matches.list.points.pending", "–", false);

    /**
     * @param match the match
     * @param bet   the bet for the match, may be <code>null</code> if the user did not bet
     */
    public static PointsBadge of(Match match, Bet bet) {
        if (!match.hasMatchFinished()) {
            return PENDING;
        }

        final int points = bet != null && bet.getPoints() != null ? bet.getPoints() : 0;
        if (points == 0) {
            // an outcome that is configured with zero points is shown like no hit at all
            return of(BetOutcome.NONE, "0", false);
        }

        return of(BetOutcome.of(match, bet), "+" + points, bet.isJoker());
    }

    private static PointsBadge of(BetOutcome betOutcome, String text, boolean jokerHint) {
        final String cssClass = "points-" + betOutcome.name().toLowerCase().replace('_', '-');
        return new PointsBadge(cssClass, betOutcome.getMsgKey(), text, jokerHint);
    }
}
