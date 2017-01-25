package de.fred4jupiter.fredbet.service.image;

import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import de.fred4jupiter.fredbet.domain.Image;
import de.fred4jupiter.fredbet.domain.ImageGroup;
import de.fred4jupiter.fredbet.repository.ImageGroupRepository;
import de.fred4jupiter.fredbet.repository.ImageRepository;
import de.fred4jupiter.fredbet.web.image.ImageCommand;
import de.fred4jupiter.fredbet.web.image.Rotation;

@Service
@Transactional
public class ImageUploadService {

	@Autowired
	private ImageRepository imageRepository;

	@Autowired
	private ImageGroupRepository imageGroupRepository;

	@Autowired
	private ImageResizingService imageResizingService;

	public void saveImageInDatabase(byte[] binary, String galleryGroup, String description, Rotation rotation) {
		ImageGroup imageGroup = imageGroupRepository.findByName(galleryGroup);

		if (imageGroup == null) {
			imageGroup = new ImageGroup(galleryGroup);
			imageGroupRepository.save(imageGroup);
		}

		byte[] thumbnail = imageResizingService.createThumbnail(binary, rotation);
		byte[] imageByte = imageResizingService.minimizeToDefaultSize(binary, rotation);

		Image image = new Image(imageByte, imageGroup, thumbnail);
		image.setDescription(description);
		imageRepository.save(image);
	}

	public List<ImageCommand> fetchAllImages() {
		List<Image> allSavedImages = imageRepository.findAll();
		if (allSavedImages.isEmpty()) {
			return Collections.emptyList();
		}
		return allSavedImages.stream().map(image -> toImageCommand(image)).collect(Collectors.toList());
	}

	private ImageCommand toImageCommand(Image image) {
		ImageCommand imageCommand = new ImageCommand();
		imageCommand.setDescription(image.getDescription());
		imageCommand.setGalleryGroup(image.getImageGroup().getName());
		imageCommand.setImageId(image.getId());
		imageCommand.setThumbImageAsBase64(Base64.getEncoder().encodeToString(image.getThumbImageBinary()));
		return imageCommand;
	}

	public byte[] loadImageById(Long imageId) {
		Image image = imageRepository.findOne(imageId);
		if (image == null) {
			return null;
		}

		return image.getImageBinary();
	}

	public void deleteImageById(Long imageId) {
		imageRepository.delete(imageId);
	}
}
