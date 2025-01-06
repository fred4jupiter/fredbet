package de.fred4jupiter.fredbet.web;

import de.fred4jupiter.fredbet.domain.Country;
import de.fred4jupiter.fredbet.domain.Group;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.time.LocalDateTime;

public abstract class AbstractMatchHeaderCommand {

    protected Country countryTeamOne;
    protected Country countryTeamTwo;

    protected LocalDateTime kickOffDate;

    private String teamNameOne;
    private String teamNameTwo;

    protected Group group;

    protected String stadium;

    public boolean hasMatchStarted() {
        return LocalDateTime.now().isAfter(getKickOffDate());
    }

    protected boolean isBlank(Country country) {
        return !isNotBlank(country);
    }

    protected boolean isNotBlank(Country country) {
        if (country == null) {
            return false;
        }

        if (Country.NONE.equals(country)) {
            return false;
        }

        return true;
    }

    @Override
    public String toString() {
        ToStringBuilder builder = new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE);
        builder.append("countryTeamOne", countryTeamOne);
        builder.append("countryTeamTwo", countryTeamTwo);
        return builder.toString();
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

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
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

}
