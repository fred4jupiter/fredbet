package de.fred4jupiter.fredbet.web;

import de.fred4jupiter.fredbet.domain.Country;
import de.fred4jupiter.fredbet.domain.Group;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public abstract class AbstractMatchHeaderCommand {

    private static final String DATE_FORMAT_PATTERN = "dd.MM.yyyy HH:mm";

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

    public String getIconPathTeamOne() {
        if (this.countryTeamOne == null || Country.NONE.equals(this.countryTeamOne)) {
            return null;
        }

        return this.countryTeamOne.getIconPath();
    }

    public String getIconPathTeamTwo() {
        if (this.countryTeamTwo == null || Country.NONE.equals(this.countryTeamTwo)) {
            return null;
        }

        return this.countryTeamTwo.getIconPath();
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

    public boolean isDateOrTimeEmpty() {
        if (kickOffDate == null) {
            return true;
        }

        return false;
    }

    public LocalDateTime getKickOffDate() {
        return kickOffDate;
    }

    public void setKickOffDate(LocalDateTime kickOffDate) {
        this.kickOffDate = kickOffDate;
    }

    public String getKickOffDateString() {
        if (kickOffDate == null) {
            return "";
        }
        return kickOffDate.format(DateTimeFormatter.ofPattern(DATE_FORMAT_PATTERN));
    }

    public void setKickOffDateString(String kickOffDateString) {
        if (StringUtils.isBlank(kickOffDateString)) {
            return;
        }
        this.kickOffDate = LocalDateTime.parse(kickOffDateString, DateTimeFormatter.ofPattern(DATE_FORMAT_PATTERN));
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
