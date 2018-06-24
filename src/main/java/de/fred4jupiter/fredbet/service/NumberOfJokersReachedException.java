package de.fred4jupiter.fredbet.service;

public class NumberOfJokersReachedException extends RuntimeException {
    private static final long serialVersionUID = 527067362155422339L;

    public NumberOfJokersReachedException(String message) {
        super(message);
    }
}
