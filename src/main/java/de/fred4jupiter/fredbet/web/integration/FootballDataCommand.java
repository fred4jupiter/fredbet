package de.fred4jupiter.fredbet.web.integration;

import de.fred4jupiter.fredbet.integration.Competition;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class FootballDataCommand {

    private boolean enabled;

    private String apiToken;

    private String competitionKey;

    private List<Competition> competitions;

    public boolean isReadyToFetchCompetitions() {
        return enabled && StringUtils.isNotBlank(apiToken);
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getCompetitionKey() {
        return competitionKey;
    }

    public void setCompetitionKey(String competitionKey) {
        this.competitionKey = competitionKey;
    }

    public String getApiToken() {
        return apiToken;
    }

    public void setApiToken(String apiToken) {
        this.apiToken = apiToken;
    }

    public List<Competition> getCompetitions() {
        return competitions;
    }

    public void setCompetitions(List<Competition> competitions) {
        this.competitions = competitions;
    }
}
