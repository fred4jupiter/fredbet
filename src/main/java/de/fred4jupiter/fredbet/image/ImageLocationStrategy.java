package de.fred4jupiter.fredbet.image;

/**
 * Locator strategy for maintaining image storage.
 *
 * @author michael
 */
public interface ImageLocationStrategy {

    void saveImage(String imageKey, Long imageGroupId, byte[] imageBinary, byte[] thumbImageBinary);

    /**
     * Delete image and thumbnail by given key.
     *
     * @param imageKey     the image key
     * @param imageGroupId the image group id
     */
    void deleteImage(String imageKey, Long imageGroupId);

    BinaryImage getImageByKey(String imageKey, Long imageGroupId);

    BinaryImage getThumbnailByKey(String imageKey, Long imageGroupId);
}
