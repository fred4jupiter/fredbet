package de.fred4jupiter.fredbet.team;

public class TeamInUseException extends RuntimeException {

    public TeamInUseException(String message) {
        super(message);
    }
}
