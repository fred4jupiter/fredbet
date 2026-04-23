package de.fred4jupiter.fredbet.web.matches;

import de.fred4jupiter.fredbet.domain.entity.Bet;
import de.fred4jupiter.fredbet.domain.entity.Match;
import de.fred4jupiter.fredbet.props.FredbetConstants;
import de.fred4jupiter.fredbet.web.AbstractMatchHeaderCommand;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class MatchCommand extends AbstractMatchHeaderCommand {

    private static final String LABEL_INFO = "label-info";

    private static final String LABEL_SUCCESS = "label-success";

    private static final String LABEL_DEFAULT = "label-default";

    private static final String LABEL_INFO_PENALTY = LABEL_INFO + " " + FredbetConstants.BADGE_PENALTY_WINNER_MATCH_CSS_CLASS;

    private Integer teamResultOne;

    private Integer teamResultTwo;

    private Integer userBetGoalsTeamOne;

    private Integer userBetGoalsTeamTwo;

    private boolean joker;

    private boolean penaltyWinnerOneBet;

    private boolean penaltyWinnerOneMatch;

    private Match match;

    private Bet bet;

    public Integer getTeamResultOne() {
        return teamResultOne;
    }

    public void setTeamResultOne(Integer teamResultOne) {
        this.teamResultOne = teamResultOne;
    }

    public Integer getTeamResultTwo() {
        return teamResultTwo;
    }

    public void setTeamResultTwo(Integer teamResultTwo) {
        this.teamResultTwo = teamResultTwo;
    }

    @Override
    public String toString() {
        ToStringBuilder builder = new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE);
        builder.appendSuper(super.toString());
        builder.append("teamResultOne", teamResultOne);
        builder.append("teamResultTwo", teamResultTwo);
        builder.append("group", group);
        builder.append("kickOffDate", getKickOffDate());
        return builder.toString();
    }

    public Integer getUserBetGoalsTeamOne() {
        return userBetGoalsTeamOne;
    }

    public void setUserBetGoalsTeamOne(Integer userBetgoalsTeamOne) {
        this.userBetGoalsTeamOne = userBetgoalsTeamOne;
    }

    public Integer getUserBetGoalsTeamTwo() {
        return userBetGoalsTeamTwo;
    }

    public void setUserBetGoalsTeamTwo(Integer userBetGoalsTeamTwo) {
        this.userBetGoalsTeamTwo = userBetGoalsTeamTwo;
    }

    public Integer getPoints() {
        if (this.match.hasMatchFinished() && this.bet == null) {
            return 0;
        }
        return this.bet.getPoints();
    }

    public boolean isGroupMatch() {
        return this.group.name().startsWith("GROUP");
    }

    public boolean isPenaltyWinnerOneBet() {
        return penaltyWinnerOneBet;
    }

    public void setPenaltyWinnerOneBet(boolean penaltyWinnerOneBet) {
        this.penaltyWinnerOneBet = penaltyWinnerOneBet;
    }

    public boolean isPenaltyWinnerOneMatch() {
        return penaltyWinnerOneMatch;
    }

    public void setPenaltyWinnerOneMatch(boolean penaltyWinnerOneMatch) {
        this.penaltyWinnerOneMatch = penaltyWinnerOneMatch;
    }

    public boolean isUndecidedBetting() {
        return getGoalDifferenceBetting() == 0;
    }

    private Integer getGoalDifferenceBetting() {
        if (this.bet == null) {
            throw new IllegalStateException("No goal bets set!");
        }
        return this.bet.getGoalDifference();
    }

    public String getUserBetGoalsTeamOneCssClasses() {
        if (this.bet == null || this.bet.getGoalsTeamOne() == null) {
            return LABEL_DEFAULT;
        }

        String cssClasses = LABEL_SUCCESS;
        if (!this.match.isGroupMatch() && this.bet.isUndecidedBetting() && this.bet.isPenaltyWinnerOne()) {
            cssClasses = cssClasses + " " + FredbetConstants.BADGE_PENALTY_WINNER_BET_CSS_CLASS;
        }

        if (isJoker()) {
            cssClasses = cssClasses + " " + FredbetConstants.JOKER_CSS_CLASS;
        }

        return cssClasses;
    }

    public String getUserBetGoalsTeamTwoCssClasses() {
        if (this.bet == null || this.bet.getGoalsTeamTwo() == null) {
            return LABEL_DEFAULT;
        }

        String cssClasses = LABEL_SUCCESS;
        if (!this.match.isGroupMatch() && this.bet.isUndecidedBetting() && !this.bet.isPenaltyWinnerOne()) {
            cssClasses = cssClasses + " " + FredbetConstants.BADGE_PENALTY_WINNER_BET_CSS_CLASS;
        }

        if (isJoker()) {
            cssClasses = cssClasses + " " + FredbetConstants.JOKER_CSS_CLASS;
        }

        return cssClasses;
    }

    public String getTeamResultOneCssClasses() {
        if (this.getTeamResultOne() == null) {
            return LABEL_DEFAULT;
        }

        return !this.match.isGroupMatch() && this.match.isUndecidedResult() && this.match.isPenaltyWinnerOne() ? LABEL_INFO_PENALTY : LABEL_INFO;
    }

    public String getTeamResultTwoCssClasses() {
        if (this.getTeamResultTwo() == null) {
            return LABEL_DEFAULT;
        }

        return !this.match.isGroupMatch() && this.match.isUndecidedResult() && !this.match.isPenaltyWinnerOne() ? LABEL_INFO_PENALTY : LABEL_INFO;
    }

    public boolean isJoker() {
        return joker;
    }

    public void setJoker(boolean joker) {
        this.joker = joker;
    }

    public Match getMatch() {
        return match;
    }

    public void setMatch(Match match) {
        this.match = match;
    }

    public Bet getBet() {
        return bet;
    }

    public void setBet(Bet bet) {
        this.bet = bet;
    }
}
