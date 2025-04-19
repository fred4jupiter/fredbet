package de.fred4jupiter.fredbet.web.matches;

import de.fred4jupiter.fredbet.domain.Country;
import de.fred4jupiter.fredbet.domain.Group;
import de.fred4jupiter.fredbet.web.validation.ValidMatchConstraint;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@ValidMatchConstraint
public class CreateEditMatchCommand {

    private Long matchId;

    private String teamNameOne;
    private String teamNameTwo;

    private Country countryTeamOne;

    private Country countryTeamTwo;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    @NotNull
    private LocalDateTime kickOffDate;

    @NotNull
    private Group group;

    private String stadium;

    private boolean deletable;

    public Long getMatchId() {
        return matchId;
    }

    public void setMatchId(Long matchId) {
        this.matchId = matchId;
    }

    public String getTeamNameOne() {
        return teamNameOne;
    }

    public void setTeamNameOne(String teamNameOne) {
        this.teamNameOne = teamNameOne;
    }

    public String getTeamNameTwo() {
        return teamNameTwo;
    }

    public void setTeamNameTwo(String teamNameTwo) {
        this.teamNameTwo = teamNameTwo;
    }

    public Country getCountryTeamOne() {
        return countryTeamOne;
    }

    public void setCountryTeamOne(Country countryTeamOne) {
        this.countryTeamOne = countryTeamOne;
    }

    public Country getCountryTeamTwo() {
        return countryTeamTwo;
    }

    public void setCountryTeamTwo(Country countryTeamTwo) {
        this.countryTeamTwo = countryTeamTwo;
    }

    public LocalDateTime getKickOffDate() {
        return kickOffDate;
    }

    public void setKickOffDate(LocalDateTime kickOffDate) {
        this.kickOffDate = kickOffDate;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public String getStadium() {
        return stadium;
    }

    public void setStadium(String stadium) {
        this.stadium = stadium;
    }

    public boolean isDeletable() {
        return deletable;
    }

    public void setDeletable(boolean deletable) {
        this.deletable = deletable;
    }
}
