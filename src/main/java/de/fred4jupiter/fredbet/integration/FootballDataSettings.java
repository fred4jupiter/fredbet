package de.fred4jupiter.fredbet.integration;

public class FootballDataSettings {

    public static final Long ID = 2L;

    private boolean enabled;

    private String competitionCode; // e.g. EC, WC

    private Integer seasonYear; // e.g. the year like

    public String getKey() {
        return competitionCode + "_" + seasonYear;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

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
}
