package de.fred4jupiter.fredbet.service.image;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.fred4jupiter.fredbet.domain.ImageBinary;
import de.fred4jupiter.fredbet.repository.ImageBinaryRepository;

@Service
public class DatabaseImageLocationService implements ImageLocationService {

	@Autowired
	private ImageBinaryRepository imageBinaryRepository;

	@Override
	public ImageData getImageDataByKey(String imageKey) {
		ImageBinary imageBinary = imageBinaryRepository.findOne(imageKey);
		if (imageBinary == null) {
			return null;
		}
		
		return toImageData(imageBinary);
	}

	private ImageData toImageData(ImageBinary imageBinary) {
		return new BinaryImageData(imageBinary.getKey(), imageBinary.getImageBinary(), imageBinary.getThumbImageBinary());
	}

	@Override
	public List<ImageData> findImagesInGroup(String imageGroup) {
		List<ImageBinary> result = imageBinaryRepository.findByImageGroup(imageGroup);
		return result.stream().map(imageBinary -> toImageData(imageBinary)).collect(Collectors.toList());
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
	public void deleteImage(String imageKey) {
		imageBinaryRepository.delete(imageKey);
	}

}
