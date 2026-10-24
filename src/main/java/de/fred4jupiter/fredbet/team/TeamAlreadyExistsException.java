package de.fred4jupiter.fredbet.team;

public class TeamAlreadyExistsException extends RuntimeException {

    public TeamAlreadyExistsException(String message) {
        super(message);
    }
}
