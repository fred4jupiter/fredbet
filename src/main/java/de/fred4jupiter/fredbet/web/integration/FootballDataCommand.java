package de.fred4jupiter.fredbet.web.integration;

public class FootballDataCommand {

    private boolean enabled;

    private String competitionCode; // e.g. EC, WC

    private Integer seasonYear; // e.g. the year like 2026, 2024

    public String getCompetitionCode() {
        return competitionCode;
    }

    public void setCompetitionCode(String competitionCode) {
        this.competitionCode = competitionCode;
    }

    public Integer getSeasonYear() {
        return seasonYear;
    }

    public void setSeasonYear(Integer seasonYear) {
        this.seasonYear = seasonYear;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
