package de.fred4jupiter.fredbet.service.image;

import java.util.List;

/**
 * Locator strategy for maintaining image storage.
 * 
 * @author michael
 *
 */
public interface ImageLocationService {

	void saveImage(String imageKey, String imageGroup, byte[] imageBinary, byte[] thumbImageBinary);

	ImageData getImageDataByKey(String imageKey, String imageGroup);

	List<ImageData> findImagesInGroup(String imageGroup);

	List<ImageData> findAllImages();

	void deleteImage(String imageKey, String imageGroup);
}
