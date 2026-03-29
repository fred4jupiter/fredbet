package de.fred4jupiter.fredbet.integration.model;

import java.time.ZonedDateTime;

public record FdMatch(FdSeason season, String id, FdTeam homeTeam, FdTeam awayTeam, String group, ZonedDateTime utcDate,
                      FdScore score,
                      ZonedDateTime lastUpdated, String stage, String status, String venue) {

    public FdMatch createNewWithGoals(Integer home, Integer away) {
        FdScore score = new FdScore(new FdFullTime(home, away));
        return new FdMatch(this.season(), this.id(), this.homeTeam(), this.awayTeam(), this.group(), this.utcDate(), score,
            ZonedDateTime.now(), this.stage(), this.status(), this.venue());
    }

    public boolean isFinished() {
        return "FINISHED".equalsIgnoreCase(status);
    }

    public boolean isUpdatedAfter(ZonedDateTime givenDate) {
        if (givenDate == null) {
            return false;
        }
        return this.lastUpdated.isAfter(givenDate);
    }
}
