package de.fred4jupiter.fredbet.service.image.storage;

public class AwsS3AccessException extends RuntimeException {

    public AwsS3AccessException(String message, Throwable cause) {
        super(message, cause);
    }

}
