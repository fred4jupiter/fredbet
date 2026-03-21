package de.fred4jupiter.fredbet.integration.model;

import java.time.ZonedDateTime;

public record FdMatch(String id, FdTeam homeTeam, FdTeam awayTeam, String group, ZonedDateTime utcDate, FdScore score,
                      ZonedDateTime lastUpdated, String stage, String status, String venue) {

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
