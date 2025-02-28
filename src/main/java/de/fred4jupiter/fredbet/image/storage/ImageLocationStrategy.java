package de.fred4jupiter.fredbet.image.storage;

import de.fred4jupiter.fredbet.image.BinaryImage;

import java.util.List;

/**
 * Locator strategy for maintaining image storage.
 *
 * @author michael
 */
public interface ImageLocationStrategy {

    String THUMBNAIL_PREFIX = "TN_";

    String IMAGE_PREFIX = "IM_";

    void saveImage(String imageKey, Long imageGroupId, byte[] imageBinary, byte[] thumbImageBinary);

    /**
     * For downloading all images as zip file.
     *
     * @return list of binary images.
     */
    List<BinaryImage> findAllImages();

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
