package de.fred4jupiter.fredbet.web.matches;

import de.fred4jupiter.fredbet.domain.entity.Match;
import de.fred4jupiter.fredbet.web.bet.RedirectViewName;
import de.fred4jupiter.fredbet.web.validation.TeamResultConstraint;

@TeamResultConstraint(message = "{msg.invalidTeamResult}")
public class MatchResultCommand {

    private Long matchId;

    private Match match;

    private Integer teamResultOne;

    private Integer teamResultTwo;

    private boolean penaltyWinnerOne;

    private boolean knockoutMatch;

    private String redirectViewName;

    public String getBackUrl() {
        String view = RedirectViewName.resolveBackUrl(redirectViewName);
        return view + "#" + getMatchId();
    }

    public String getRedirectViewName() {
        return redirectViewName;
    }

    public void setRedirectViewName(String redirectViewName) {
        this.redirectViewName = redirectViewName;
    }

    public Long getMatchId() {
        return matchId;
    }

    public void setMatchId(Long matchId) {
        this.matchId = matchId;
    }

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

    public boolean isPenaltyWinnerOne() {
        return penaltyWinnerOne;
    }

    public void setPenaltyWinnerOne(boolean penaltyWinnerOne) {
        this.penaltyWinnerOne = penaltyWinnerOne;
    }

    public Match getMatch() {
        return match;
    }

    public void setMatch(Match match) {
        this.match = match;
    }

    public boolean isKnockoutMatch() {
        return knockoutMatch;
    }

    public void setKnockoutMatch(boolean knockoutMatch) {
        this.knockoutMatch = knockoutMatch;
    }
}
