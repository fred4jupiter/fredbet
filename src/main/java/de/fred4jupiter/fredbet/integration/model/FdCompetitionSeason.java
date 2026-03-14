package de.fred4jupiter.fredbet.integration.model;

import java.time.LocalDate;

public record FdCompetitionSeason(String id, LocalDate startDate, LocalDate endDate) {

    public Integer getSeasonYear() {
        return startDate.getYear();
    }
}
