package de.fred4jupiter.fredbet.web.matches;

import de.fred4jupiter.fredbet.domain.entity.Bet;
import de.fred4jupiter.fredbet.domain.entity.Match;
import de.fred4jupiter.fredbet.points.BetOutcome;
import de.fred4jupiter.fredbet.props.FredbetConstants;
import de.fred4jupiter.fredbet.util.MessageSourceUtil;
import org.apache.commons.lang3.StringUtils;

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

    /**
     * Outcome of the bet of the current user, or <code>null</code> as long as the match has no result.
     */
    public BetOutcome getBetOutcome() {
        if (!this.match.hasMatchFinished()) {
            return null;
        }
        return this.betOpt.filter(bet -> bet.getGoalsTeamOne() != null && bet.getGoalsTeamTwo() != null)
            .map(bet -> BetOutcome.of(this.match, bet))
            .orElse(BetOutcome.NONE);
    }

    public String getPointsCssClass() {
        final BetOutcome betOutcome = getRewardedBetOutcome();
        return betOutcome == null ? "points-pending" : "points-" + betOutcome.name().toLowerCase().replace('_', '-');
    }

    public String getPointsMsgKey() {
        final BetOutcome betOutcome = getRewardedBetOutcome();
        return betOutcome == null ? "matches.list.points.pending" : betOutcome.getMsgKey();
    }

    // an outcome that is configured with zero points is shown like no hit at all
    private BetOutcome getRewardedBetOutcome() {
        final BetOutcome betOutcome = getBetOutcome();
        if (betOutcome != null && getPoints() == 0) {
            return BetOutcome.NONE;
        }
        return betOutcome;
    }

    public boolean isJokerBet() {
        return this.betOpt.map(Bet::isJoker).orElse(false);
    }

    public String getUserBetGoalsTeamOneCssClasses() {
        if (this.betOpt.isEmpty() || this.betOpt.get().getGoalsTeamOne() == null) {
            return LABEL_DEFAULT;
        }

        final Bet bet = this.betOpt.get();

        String cssClasses = LABEL_SUCCESS;
        if (this.match.isKnockoutMatch() && bet.isUndecidedBetting() && bet.isPenaltyWinnerOne()) {
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
        if (this.match.isKnockoutMatch() && bet.isUndecidedBetting() && !bet.isPenaltyWinnerOne()) {
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

        return this.match.isKnockoutMatch() && this.match.isUndecidedResult() && this.match.isPenaltyWinnerOne() ? LABEL_INFO_PENALTY : LABEL_INFO;
    }

    public String getTeamResultTwoCssClasses() {
        if (this.match.getGoalsTeamTwo() == null) {
            return LABEL_DEFAULT;
        }

        return this.match.isKnockoutMatch() && this.match.isUndecidedResult() && !this.match.isPenaltyWinnerOne() ? LABEL_INFO_PENALTY : LABEL_INFO;
    }

    public Match getMatch() {
        return match;
    }

    public Bet getBet() {
        return this.betOpt.orElse(null);
    }
}
