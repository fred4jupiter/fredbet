package de.fred4jupiter.fredbet.service.user;

public class UserNotDeletableException extends RuntimeException {

    public UserNotDeletableException(String message) {
        super(message);
    }

}
