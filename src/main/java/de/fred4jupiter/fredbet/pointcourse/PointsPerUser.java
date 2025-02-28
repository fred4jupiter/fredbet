package de.fred4jupiter.fredbet.pointcourse;

public record PointsPerUser(String username, Long points) {

    public Long points() {
        return points != null ? points : 0;
    }
}
