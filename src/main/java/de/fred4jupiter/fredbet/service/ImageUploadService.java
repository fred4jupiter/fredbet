package de.fred4jupiter.fredbet.service;

import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import de.fred4jupiter.fredbet.domain.Image;
import de.fred4jupiter.fredbet.repository.ImageRepository;
import de.fred4jupiter.fredbet.web.image.ImageCommand;

@Service
@Transactional
public class ImageUploadService {

	@Autowired
	private ImageRepository fileStorageRepository;

	@Autowired
	private ImageResizingService imageResizingService;

	public void saveImageInDatabase(String fileName, byte[] binary, String galleryGroup, String description) {
		byte[] thumbnail = imageResizingService.createThumbnail(binary);
		byte[] imageByte = imageResizingService.createDefaultSizedImage(binary);

		Image image = new Image(fileName, imageByte, galleryGroup, thumbnail);
		image.setDescription(description);
		fileStorageRepository.save(image);
	}

	public List<ImageCommand> fetchAllImages() {
		List<Image> allSavedImages = fileStorageRepository.findAll();
		return allSavedImages.stream().map(image -> toImageCommand(image)).collect(Collectors.toList());
	}

	private ImageCommand toImageCommand(Image image) {
		ImageCommand imageCommand = new ImageCommand();
		imageCommand.setDescription(image.getDescription());
		imageCommand.setFileName(image.getFileName());
		imageCommand.setGalleryGroup(image.getGalleryGroup());
		imageCommand.setImageId(image.getId());
		imageCommand.setThumbImageAsBase64(Base64.getEncoder().encodeToString(image.getThumbImageBinary()));
		return imageCommand;
	}

	public byte[] loadImageById(Long imageId) {
		Image image = fileStorageRepository.findOne(imageId);
		if (image == null) {
			return null;
		}

		return image.getImageBinary();
	}
}
