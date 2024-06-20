package de.fred4jupiter.fredbet.imexport;

import com.fasterxml.jackson.annotation.JsonFormat;
import de.fred4jupiter.fredbet.domain.Group;
import de.fred4jupiter.fredbet.domain.Team;

import java.time.LocalDateTime;

class MatchToExport {

    private Team teamOne;

    private Team teamTwo;

    private Group group;

    private boolean penaltyWinnerOne;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd@HH:mm")
    private LocalDateTime kickOffDate;

    private String stadium;

    private String matchBusinessKey;

    public Team getTeamOne() {
        return teamOne;
    }

    public void setTeamOne(Team teamOne) {
        this.teamOne = teamOne;
    }

    public Team getTeamTwo() {
        return teamTwo;
    }

    public void setTeamTwo(Team teamTwo) {
        this.teamTwo = teamTwo;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public boolean isPenaltyWinnerOne() {
        return penaltyWinnerOne;
    }

    public void setPenaltyWinnerOne(boolean penaltyWinnerOne) {
        this.penaltyWinnerOne = penaltyWinnerOne;
    }

    public LocalDateTime getKickOffDate() {
        return kickOffDate;
    }

    public void setKickOffDate(LocalDateTime kickOffDate) {
        this.kickOffDate = kickOffDate;
    }

    public String getStadium() {
        return stadium;
    }

    public void setStadium(String stadium) {
        this.stadium = stadium;
    }

    public String getMatchBusinessKey() {
        return matchBusinessKey;
    }

    public void setMatchBusinessKey(String matchBusinessKey) {
        this.matchBusinessKey = matchBusinessKey;
    }
}
