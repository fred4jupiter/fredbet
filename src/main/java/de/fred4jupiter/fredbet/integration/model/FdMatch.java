package de.fred4jupiter.fredbet.integration.model;

import java.time.ZonedDateTime;

public record FdMatch(String id, FdTeam homeTeam, FdTeam awayTeam, String group, ZonedDateTime utcDate, FdScore score,
                      ZonedDateTime lastUpdated, String stage, String status) {

    public boolean isFinished() {
        return "FINISHED".equalsIgnoreCase(status);
    }
}
