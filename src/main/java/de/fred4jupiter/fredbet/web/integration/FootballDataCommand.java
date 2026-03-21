package de.fred4jupiter.fredbet.web.integration;

import org.apache.commons.lang3.StringUtils;

public class FootballDataCommand {

    private boolean enabled;

    private String apiToken;

    private String competitionKey;

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
}
