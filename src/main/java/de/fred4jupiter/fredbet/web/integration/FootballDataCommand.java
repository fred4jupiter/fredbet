package de.fred4jupiter.fredbet.web.integration;

import de.fred4jupiter.fredbet.integration.Competition;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class FootballDataCommand {

    private boolean enabled;

    private String apiToken;

    private Integer competitionId;

    private List<Competition> competitions;

    public Competition getCompetitionById(Integer competitionId) {
        if (this.competitions == null) {
            return null;
        }
        return this.competitions.stream().filter(c -> c.id().equals(competitionId)).findFirst().orElse(null);
    }

    public boolean isReadyToFetchCompetitions() {
        return enabled && StringUtils.isNotBlank(apiToken);
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
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

    public Integer getCompetitionId() {
        return competitionId;
    }

    public void setCompetitionId(Integer competitionId) {
        this.competitionId = competitionId;
    }
}
