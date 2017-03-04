package de.fred4jupiter.fredbet.service.image;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import de.fred4jupiter.fredbet.domain.ImageBinary;
import de.fred4jupiter.fredbet.repository.ImageBinaryRepository;

/**
 * Storing images of the image gallery in database.
 * 
 * @author michael
 *
 */
public class DatabaseImageLocationStrategy implements ImageLocationStrategy {

	private final ImageBinaryRepository imageBinaryRepository;

	public DatabaseImageLocationStrategy(ImageBinaryRepository imageBinaryRepository) {
		super();
		this.imageBinaryRepository = imageBinaryRepository;
	}

	@Override
	public void saveImage(String imageKey, String imageGroup, byte[] imageBytes, byte[] thumbImageBinary) {
		ImageBinary imageBinary = new ImageBinary(imageKey, imageBytes, imageGroup, thumbImageBinary);
		imageBinaryRepository.save(imageBinary);
	}

	@Override
	public List<ImageData> findAllImages() {
		List<ImageBinary> allImages = imageBinaryRepository.findAll();
		if (allImages.isEmpty()) {
			return Collections.emptyList();
		}
		return allImages.stream().map(imageBinary -> toImageData(imageBinary)).collect(Collectors.toList());
	}

	@Override
	public ImageData getImageDataByKey(String imageKey, String imageGroup) {
		ImageBinary imageBinary = imageBinaryRepository.findOne(imageKey);
		if (imageBinary == null) {
			return null;
		}

		return toImageData(imageBinary);
	}

	@Override
	public void deleteImage(String imageKey, String imageGroup) {
		imageBinaryRepository.delete(imageKey);
	}

	private ImageData toImageData(ImageBinary imageBinary) {
		return new BinaryImageData(imageBinary.getKey(), imageBinary.getImageBinary(),
				imageBinary.getThumbImageBinary());
	}
}
