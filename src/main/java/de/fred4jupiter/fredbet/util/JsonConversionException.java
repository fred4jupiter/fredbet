package de.fred4jupiter.fredbet.util;

public class JsonConversionException extends RuntimeException {

    public JsonConversionException(String message) {
        super(message);
    }

    public JsonConversionException(String message, Throwable cause) {
        super(message, cause);
    }
}
