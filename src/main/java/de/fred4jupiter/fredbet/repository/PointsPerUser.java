package de.fred4jupiter.fredbet.repository;

public class PointsPerUser {

    private final String username;

    private final Long points;

    public PointsPerUser(String username, Long points) {
        this.username = username;
        this.points = points;
    }

    public String getUsername() {
        return username;
    }

    public Long getPoints() {
        return points;
    }


}
