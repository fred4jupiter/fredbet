package de.fred4jupiter.fredbet.integration;

import jakarta.persistence.*;

@Entity
@Table(name = "football_data_settings")
public class FootballDataSettings {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;

    @Column(nullable = false)
    private boolean enabled;

    @Column(nullable = false)
    private String competitionCode; // e.g. EC, WC

    @Column(nullable = false)
    private Integer seasonYear; // e.g. the year like

    public Long getId() {
        return id;
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
