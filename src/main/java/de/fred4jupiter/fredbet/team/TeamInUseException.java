package de.fred4jupiter.fredbet.team;

public class TeamInUseException extends RuntimeException {

    private final TeamUsage teamUsage;

    public TeamInUseException(Long teamId, TeamUsage teamUsage) {
        super("Team with id=%s is still in use: %s".formatted(teamId, teamUsage));
        this.teamUsage = teamUsage;
    }

    public TeamUsage getTeamUsage() {
        return teamUsage;
    }
}
