package de.fred4jupiter.fredbet.service.image.storage;

import java.util.List;

import de.fred4jupiter.fredbet.service.image.BinaryImage;

/**
 * Locator strategy for maintaining image storage.
 * 
 * @author michael
 *
 */
public interface ImageLocationStrategy {

	String THUMBNAIL_PREFIX = "TN_";

	String IMAGE_PREFIX = "IM_";

	void saveImage(String imageKey, Long imageGroupId, byte[] imageBinary, byte[] thumbImageBinary);

	/**
	 * For downloading all images as zip file.
	 * 
	 * @return
	 */
	List<BinaryImage> findAllImages();

	/**
	 * Delete image and thumbnail by given key.
	 * 
	 * @param imageKey
	 * @param imageGroup
	 */
	void deleteImage(String imageKey, Long imageGroupId);

	BinaryImage getImageByKey(String imageKey, Long imageGroupId);

	BinaryImage getThumbnailByKey(String imageKey, Long imageGroupId);
}
