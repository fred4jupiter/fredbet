package de.fred4jupiter.fredbet.integration.model;

import java.time.ZonedDateTime;

public record FdMatch(String id, FdTeam homeTeam, FdTeam awayTeam, String group, ZonedDateTime utcDate, String venue, FdScore score) {
}
