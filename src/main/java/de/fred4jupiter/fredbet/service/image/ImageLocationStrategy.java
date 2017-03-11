package de.fred4jupiter.fredbet.service.image;

import java.util.List;

/**
 * Locator strategy for maintaining image storage.
 * 
 * @author michael
 *
 */
public interface ImageLocationStrategy {

	String THUMBNAIL_PREFIX = "TN_";

	String IMAGE_PREFIX = "IM_";

	void saveImage(String imageKey, String imageGroup, byte[] imageBinary, byte[] thumbImageBinary);

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
	void deleteImage(String imageKey, String imageGroup);

	BinaryImage getImageByKey(String imageKey, String imageGroup);

	BinaryImage getThumbnailByKey(String imageKey, String imageGroup);
}
