package de.fred4jupiter.fredbet.web.admin;

import de.fred4jupiter.fredbet.teambundle.TeamBundle;

public class TestDataCommand {

    private TeamBundle teamBundle;

    private Integer numberOfGroups;

    private Boolean includeBets;

    private Boolean includeResults;

    public TeamBundle getTeamBundle() {
        return teamBundle;
    }

    public void setTeamBundle(TeamBundle teamBundle) {
        this.teamBundle = teamBundle;
    }

    public Integer getNumberOfGroups() {
        return numberOfGroups;
    }

    public void setNumberOfGroups(Integer numberOfGroups) {
        this.numberOfGroups = numberOfGroups;
    }

    public Boolean getIncludeBets() {
        return includeBets;
    }

    public void setIncludeBets(Boolean includeBets) {
        this.includeBets = includeBets;
    }

    public Boolean getIncludeResults() {
        return includeResults;
    }

    public void setIncludeResults(Boolean includeResults) {
        this.includeResults = includeResults;
    }
}
