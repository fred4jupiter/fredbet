package de.fred4jupiter.fredbet.imexport;

import de.fred4jupiter.fredbet.domain.Group;
import de.fred4jupiter.fredbet.domain.Team;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.time.LocalDateTime;

class MatchToExport implements MatchBusinessKey {

    private Integer matchBusinessKey;

    private Team teamOne;

    private Team teamTwo;

    private Group group;

    private boolean penaltyWinnerOne;

    private LocalDateTime kickOffDate;

    private String stadium;

    public void setMatchBusinessKey(Integer matchBusinessKey) {
        this.matchBusinessKey = matchBusinessKey;
    }

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

    @Override
    public Integer getMatchBusinessKey() {
        return new HashCodeBuilder()
                .append(this.teamOne.getBusinessKey())
                .append(this.teamTwo.getBusinessKey())
                .append(this.group)
                .append(this.kickOffDate)
                .build();
    }
}
