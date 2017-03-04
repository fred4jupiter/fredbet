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

	ImageData getImageDataByKey(String imageKey, String imageGroup);

	List<ImageData> findAllImages();

	void deleteImage(String imageKey, String imageGroup);
}
