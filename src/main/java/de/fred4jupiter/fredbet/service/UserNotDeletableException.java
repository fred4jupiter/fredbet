package de.fred4jupiter.fredbet.service;

public class UserNotDeletableException extends RuntimeException {

	private static final long serialVersionUID = 793848681899164969L;

	public UserNotDeletableException(String message) {
		super(message);
	}

}
