package de.fred4jupiter.fredbet.image;

/**
 * Locator strategy for maintaining image storage.
 *
 * @author michael
 */
public interface ImageLocationStrategy {

    BinaryImage getImageByKey(String imageKey, Long imageGroupId);

    BinaryImage getThumbnailByKey(String imageKey, Long imageGroupId);
}
