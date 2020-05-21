package de.fred4jupiter.fredbet.repository;

import de.fred4jupiter.fredbet.domain.Match;

public class PointCourseResult {

    private final String username;

    private final Integer points;

    private final Match match;

    public PointCourseResult(String username, Integer points, Match match) {
        this.username = username;
        this.points = points;
        this.match = match;
    }

    public String getUsername() {
        return username;
    }

    public Integer getPoints() {
        return points;
    }

    public Match getMatch() {
        return match;
    }
}
