package de.fred4jupiter.fredbet.service.image;

public class ImageUploadLimitReachedException extends RuntimeException{

    private final Integer currentCount;

    private final Integer limit;

    public ImageUploadLimitReachedException(String message, Integer currentCount, Integer limit) {
        super(message);
        this.currentCount = currentCount;
        this.limit = limit;
    }

    public Integer getCurrentCount() {
        return currentCount;
    }

    public Integer getLimit() {
        return limit;
    }
}
