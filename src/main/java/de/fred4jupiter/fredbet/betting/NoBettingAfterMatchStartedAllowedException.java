package de.fred4jupiter.fredbet.betting;

public class NoBettingAfterMatchStartedAllowedException extends RuntimeException {

	public NoBettingAfterMatchStartedAllowedException(String message) {
		super(message);
	}

}
