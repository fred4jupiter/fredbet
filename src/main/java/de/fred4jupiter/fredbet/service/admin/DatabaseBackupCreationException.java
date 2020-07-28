package de.fred4jupiter.fredbet.service.admin;

/**
 * Exception that will be thrown if the database backup could not be created.
 *
 * @author michael
 */
public class DatabaseBackupCreationException extends RuntimeException {

    private final ErrorCode errorCode;

    public DatabaseBackupCreationException(String message, ErrorCode errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    public enum ErrorCode {
        UNSUPPORTED_DATABASE_TYPE, IN_MEMORY_NOT_SUPPORTED
    }
}
