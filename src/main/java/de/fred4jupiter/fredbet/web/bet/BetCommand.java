package de.fred4jupiter.fredbet.web.bet;

import de.fred4jupiter.fredbet.domain.entity.Match;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class BetCommand {

    private final Match match;

    private Long betId;

    @NotNull
    private Long matchId;

    @NotNull
    @Min(value = 0)
    private Integer goalsTeamOne;

    @NotNull
    @Min(value = 0)
    private Integer goalsTeamTwo;

    private String redirectViewName;

    private boolean groupMatch;

    private boolean penaltyWinnerOne;

    private boolean useJoker;

    private Integer numberOfJokersUsed;

    /*
     * maximum jokers allowed to use (general setting)
     */
    private Integer maxJokers;

    private boolean jokerEditable;

    public BetCommand(Match match) {
        this.match = match;
    }

    public String getBackUrl() {
        String view = RedirectViewName.resolveBackUrl(redirectViewName);
        return view + "#" + getMatchId();
    }

    @Override
    public String toString() {
        ToStringBuilder builder = new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE);
        builder.append("matchId", matchId);
        builder.append("betId", betId);
        builder.append("goalsTeamOne", goalsTeamOne);
        builder.append("goalsTeamTwo", goalsTeamTwo);
        builder.append("redirectViewName", redirectViewName);
        builder.append("groupMatch", groupMatch);
        builder.append("penaltyWinnerOne", penaltyWinnerOne);
        builder.append("useJoker", useJoker);
        return builder.toString();
    }

    public Integer getGoalsTeamOne() {
        return goalsTeamOne;
    }

    public void setGoalsTeamOne(Integer goalsTeamOne) {
        this.goalsTeamOne = goalsTeamOne;
    }

    public Integer getGoalsTeamTwo() {
        return goalsTeamTwo;
    }

    public void setGoalsTeamTwo(Integer goalsTeamTwo) {
        this.goalsTeamTwo = goalsTeamTwo;
    }

    public Long getBetId() {
        return betId;
    }

    public void setBetId(Long betId) {
        this.betId = betId;
    }

    public Long getMatchId() {
        return matchId;
    }

    public void setMatchId(Long matchId) {
        this.matchId = matchId;
    }

    public String getRedirectViewName() {
        return redirectViewName;
    }

    public void setRedirectViewName(String redirectViewName) {
        this.redirectViewName = redirectViewName;
    }

    public boolean isPenaltyWinnerOne() {
        return penaltyWinnerOne;
    }

    public void setPenaltyWinnerOne(boolean penaltyWinnerOne) {
        this.penaltyWinnerOne = penaltyWinnerOne;
    }

    public boolean isGroupMatch() {
        return groupMatch;
    }

    public void setGroupMatch(boolean groupMatch) {
        this.groupMatch = groupMatch;
    }

    public boolean isUseJoker() {
        return useJoker;
    }

    public void setUseJoker(boolean useJoker) {
        this.useJoker = useJoker;
    }

    public Integer getNumberOfJokersUsed() {
        return numberOfJokersUsed;
    }

    public void setNumberOfJokersUsed(Integer numberOfJokersUsed) {
        this.numberOfJokersUsed = numberOfJokersUsed;
    }

    public Integer getMaxJokers() {
        return maxJokers;
    }

    public void setMaxJokers(Integer maxJokers) {
        this.maxJokers = maxJokers;
    }

    public boolean isJokerEditable() {
        return jokerEditable;
    }

    public void setJokerEditable(boolean jokerEditable) {
        this.jokerEditable = jokerEditable;
    }

    public Match getMatch() {
        return match;
    }
}
