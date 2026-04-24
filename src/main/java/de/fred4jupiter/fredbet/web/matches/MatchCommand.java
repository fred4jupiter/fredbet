package de.fred4jupiter.fredbet.web.matches;

import de.fred4jupiter.fredbet.domain.entity.Bet;
import de.fred4jupiter.fredbet.domain.entity.Match;
import de.fred4jupiter.fredbet.props.FredbetConstants;

import java.util.Optional;

public class MatchCommand {

    private static final String LABEL_INFO = "label-info";

    private static final String LABEL_SUCCESS = "label-success";

    private static final String LABEL_DEFAULT = "label-default";

    private static final String LABEL_INFO_PENALTY = LABEL_INFO + " " + FredbetConstants.BADGE_PENALTY_WINNER_MATCH_CSS_CLASS;

    private final Match match;

    private final Optional<Bet> betOpt; // bet of current user

    public MatchCommand(Match match, Optional<Bet> betOpt) {
        this.match = match;
        this.betOpt = betOpt;
    }

    public Integer getPoints() {
        if (this.match.hasMatchFinished() && this.betOpt.isEmpty()) {
            return 0;
        }
        return this.betOpt.map(Bet::getPoints).orElse(0);
    }

    public boolean isGroupMatch() {
        return this.match.getGroup().name().startsWith("GROUP");
    }

    public String getUserBetGoalsTeamOneCssClasses() {
        if (this.betOpt.isEmpty() || this.betOpt.get().getGoalsTeamOne() == null) {
            return LABEL_DEFAULT;
        }

        final Bet bet = this.betOpt.get();

        String cssClasses = LABEL_SUCCESS;
        if (!this.match.isGroupMatch() && bet.isUndecidedBetting() && bet.isPenaltyWinnerOne()) {
            cssClasses = cssClasses + " " + FredbetConstants.BADGE_PENALTY_WINNER_BET_CSS_CLASS;
        }

        if (bet.isJoker()) {
            cssClasses = cssClasses + " " + FredbetConstants.JOKER_CSS_CLASS;
        }

        return cssClasses;
    }

    public String getUserBetGoalsTeamTwoCssClasses() {
        if (this.betOpt.isEmpty() || this.betOpt.get().getGoalsTeamTwo() == null) {
            return LABEL_DEFAULT;
        }

        final Bet bet = this.betOpt.get();

        String cssClasses = LABEL_SUCCESS;
        if (!this.match.isGroupMatch() && bet.isUndecidedBetting() && !bet.isPenaltyWinnerOne()) {
            cssClasses = cssClasses + " " + FredbetConstants.BADGE_PENALTY_WINNER_BET_CSS_CLASS;
        }

        if (bet.isJoker()) {
            cssClasses = cssClasses + " " + FredbetConstants.JOKER_CSS_CLASS;
        }

        return cssClasses;
    }

    public String getTeamResultOneCssClasses() {
        if (this.match.getGoalsTeamOne() == null) {
            return LABEL_DEFAULT;
        }

        return !this.match.isGroupMatch() && this.match.isUndecidedResult() && this.match.isPenaltyWinnerOne() ? LABEL_INFO_PENALTY : LABEL_INFO;
    }

    public String getTeamResultTwoCssClasses() {
        if (this.match.getGoalsTeamTwo() == null) {
            return LABEL_DEFAULT;
        }

        return !this.match.isGroupMatch() && this.match.isUndecidedResult() && !this.match.isPenaltyWinnerOne() ? LABEL_INFO_PENALTY : LABEL_INFO;
    }

    public Match getMatch() {
        return match;
    }

    public Bet getBet() {
        return this.betOpt.orElse(null);
    }
}
