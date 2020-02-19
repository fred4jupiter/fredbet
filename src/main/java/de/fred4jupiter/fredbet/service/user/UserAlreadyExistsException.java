package de.fred4jupiter.fredbet.service.user;

public class UserAlreadyExistsException extends RuntimeException {

    private static final long serialVersionUID = 450175873556819001L;

    public UserAlreadyExistsException(String message) {
        super(message);
    }

}
