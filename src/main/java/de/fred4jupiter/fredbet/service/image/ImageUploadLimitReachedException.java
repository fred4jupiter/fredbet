package de.fred4jupiter.fredbet.service.image;

public class ImageUploadLimitReachedException extends RuntimeException {

    private final Integer limit;

    public ImageUploadLimitReachedException(String message, Integer limit) {
        super(message);
        this.limit = limit;
    }

    public Integer getLimit() {
        return limit;
    }
}
