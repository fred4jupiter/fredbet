package de.fred4jupiter.fredbet.service;

public class NoBettingAfterMatchStartedAllowedException extends RuntimeException {

	private static final long serialVersionUID = 5270673621314210679L;

	public NoBettingAfterMatchStartedAllowedException(String message) {
		super(message);
	}

}
