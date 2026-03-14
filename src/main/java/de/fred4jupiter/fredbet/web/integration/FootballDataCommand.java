package de.fred4jupiter.fredbet.web.integration;

public class FootballDataCommand {

    private boolean enabled;

    private String competitionKey;

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
}
