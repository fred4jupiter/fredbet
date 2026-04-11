package de.fred4jupiter.fredbet.integration.model;

import java.time.ZonedDateTime;

public record FdMatch(FdSeason season, String id, FdTeam homeTeam, FdTeam awayTeam, FdMatchGroup group,
                      ZonedDateTime utcDate,
                      FdScore score,
                      ZonedDateTime lastUpdated, FdMatchStage stage, FdMatchStatus status, String venue) {

    public FdMatch createNewWithGoals(Integer home, Integer away) {
        final FdScore oldScore = this.score;
        final FdGoals fullTimeGoals = new FdGoals(home, away);
        final FdScore newScore = new FdScore(oldScore.winner(), oldScore.duration(), fullTimeGoals, oldScore.regularTime());
        return new FdMatch(this.season(), this.id(), this.homeTeam(), this.awayTeam(), this.group(), this.utcDate(), newScore,
            ZonedDateTime.now(), this.stage(), this.status(), this.venue());
    }

    public boolean isFinished() {
        return FdMatchStatus.FINISHED.equals(status);
    }

    public boolean isUpdatedAfter(ZonedDateTime givenDate) {
        if (givenDate == null) {
            return false;
        }
        return this.lastUpdated.isAfter(givenDate);
    }
}
