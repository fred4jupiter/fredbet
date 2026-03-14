package de.fred4jupiter.fredbet.integration;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class FootballDataSettings {

    public static final Long ID = 2L;

    private static final String KEY_SEPARATOR = "_";

    private boolean enabled;

    private String competitionCode; // e.g. EC, WC

    private Integer seasonYear; // e.g. the year like

    public static FootballDataSettings fromKey(boolean enabled, String key) {
        String code = key.split(KEY_SEPARATOR)[0];
        int seasonYear = Integer.parseInt(key.split(KEY_SEPARATOR)[1]);

        FootballDataSettings settings = new FootballDataSettings();
        settings.setEnabled(enabled);
        settings.setCompetitionCode(code);
        settings.setSeasonYear(seasonYear);
        return settings;
    }

    public String getKey() {
        return competitionCode + KEY_SEPARATOR + seasonYear;
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

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
            .append("enabled", enabled)
            .append("competitionCode", competitionCode)
            .append("seasonYear", seasonYear)
            .toString();
    }
}
