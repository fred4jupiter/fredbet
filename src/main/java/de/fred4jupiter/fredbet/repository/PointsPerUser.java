package de.fred4jupiter.fredbet.repository;

public record PointsPerUser(String username, Long points) {

    public Long points() {
        return points != null ? points : 0;
    }
}
