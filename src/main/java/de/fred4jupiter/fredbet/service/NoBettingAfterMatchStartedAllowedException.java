package de.fred4jupiter.fredbet.service;

public class NoBettingAfterMatchStartedAllowedException extends RuntimeException {

	public NoBettingAfterMatchStartedAllowedException(String message) {
		super(message);
	}

}
