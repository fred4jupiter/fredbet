package de.fred4jupiter.fredbet.service.image.storage;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import de.fred4jupiter.fredbet.domain.ImageBinary;
import de.fred4jupiter.fredbet.repository.ImageBinaryRepository;
import de.fred4jupiter.fredbet.service.image.BinaryImage;

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
	public void saveImage(String imageKey, Long imageGroupId, byte[] imageBytes, byte[] thumbImageBinary) {
		ImageBinary imageBinary = new ImageBinary(imageKey, imageBytes, imageGroupId, thumbImageBinary);
		imageBinaryRepository.save(imageBinary);
	}

	@Override
	public List<BinaryImage> findAllImages() {
		List<ImageBinary> allImages = imageBinaryRepository.findAll();
		if (allImages.isEmpty()) {
			return Collections.emptyList();
		}
		return allImages.stream().map(imageBinary -> toImageData(imageBinary)).collect(Collectors.toList());
	}

	private BinaryImage toImageData(ImageBinary imageBinary) {
		return new BinaryImage(imageBinary.getKey(), imageBinary.getImageBinary());
	}

	@Override
	public BinaryImage getImageByKey(String imageKey, Long imageGroupId) {
		ImageBinary imageBinary = findOne(imageKey);
		return new BinaryImage(imageBinary.getKey(), imageBinary.getImageBinary());
	}

	private ImageBinary findOne(String imageKey) {
		Optional<ImageBinary> imageBinaryOpt = imageBinaryRepository.findById(imageKey);
		if (!imageBinaryOpt.isPresent()) {
			return null;
		}

		return imageBinaryOpt.get();
	}

	@Override
	public BinaryImage getThumbnailByKey(String imageKey, Long imageGroupId) {
		ImageBinary imageBinary = findOne(imageKey);
		return new BinaryImage(imageBinary.getKey(), imageBinary.getThumbImageBinary());
	}

	@Override
	public void deleteImage(String imageKey, Long imageGroupId) {
		ImageBinary imageBinary = imageBinaryRepository.findByKeyAndImageGroupId(imageKey, imageGroupId);
		if (imageBinary != null) {
			imageBinaryRepository.delete(imageBinary);
		}
	}

}
