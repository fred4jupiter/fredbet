package de.fred4jupiter.fredbet.service.image.storage;

public class AwsS3AccessException extends RuntimeException {

	private static final long serialVersionUID = 230352617542567913L;

	public AwsS3AccessException(String message) {
		super(message);
	}

	public AwsS3AccessException(String message, Throwable cause) {
		super(message, cause);
	}

}
