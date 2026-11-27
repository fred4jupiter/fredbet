package de.fred4jupiter.fredbet.csvimport;

/**
 * Thrown if an uploaded CSV file cannot be read. Carries the key of a translated message for the user.
 */
public class CsvImportException extends RuntimeException {

    private final String msgKey;

    public CsvImportException(String msgKey, String message) {
        super(message);
        this.msgKey = msgKey;
    }

    public String getMsgKey() {
        return msgKey;
    }
}
