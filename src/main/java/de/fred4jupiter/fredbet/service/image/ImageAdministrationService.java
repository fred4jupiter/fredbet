package de.fred4jupiter.fredbet.service.image;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import de.fred4jupiter.fredbet.domain.AppUser;
import de.fred4jupiter.fredbet.domain.ImageGroup;
import de.fred4jupiter.fredbet.domain.ImageMetaData;
import de.fred4jupiter.fredbet.repository.AppUserRepository;
import de.fred4jupiter.fredbet.repository.ImageGroupRepository;
import de.fred4jupiter.fredbet.repository.ImageMetaDataRepository;
import de.fred4jupiter.fredbet.security.SecurityService;
import de.fred4jupiter.fredbet.service.image.storage.ImageLocationStrategy;
import de.fred4jupiter.fredbet.web.image.ImageCommand;
import de.fred4jupiter.fredbet.web.image.Rotation;

@Service
@Transactional
public class ImageAdministrationService {

	private static final Logger LOG = LoggerFactory.getLogger(ImageAdministrationService.class);

	@Autowired
	private ImageMetaDataRepository imageMetaDataRepository;

	@Autowired
	private ImageGroupRepository imageGroupRepository;

	@Autowired
	private ImageResizingService imageResizingService;

	@Autowired
	private ImageLocationStrategy imageLocationService;

	@Autowired
	private ImageKeyGenerator imageKeyGenerator;

	@Autowired
	private AppUserRepository appUserRepository;
	
	@Autowired
	private SecurityService securityService;

	public void saveImage(byte[] binary, String galleryGroup, String description, Rotation rotation) {
		ImageGroup imageGroup = createImageGroup(galleryGroup);

		byte[] thumbnail = imageResizingService.createThumbnail(binary, rotation);
		byte[] imageByte = imageResizingService.minimizeToDefaultSize(binary, rotation);

		final String key = imageKeyGenerator.generateKey();

		ImageMetaData image = new ImageMetaData(key, imageGroup, securityService.getCurrentUser());
		image.setDescription(description);
		imageMetaDataRepository.save(image);

		imageLocationService.saveImage(key, imageGroup.getName(), imageByte, thumbnail);
	}

	public ImageGroup createImageGroup(String galleryGroup) {
		ImageGroup imageGroup = imageGroupRepository.findByName(galleryGroup);

		if (imageGroup == null) {
			imageGroup = new ImageGroup(galleryGroup);
			imageGroupRepository.save(imageGroup);
		}
		return imageGroup;
	}

	public List<ImageCommand> fetchAllImages() {
		List<ImageMetaData> imageMetaDataList = imageMetaDataRepository.findAll();
		return toListOfImageCommand(imageMetaDataList);
	}

	public List<ImageCommand> fetchImagesOfUser(String currentUserName) {
		LOG.debug("fetching images of user={}", currentUserName);
		AppUser appUser = appUserRepository.findByUsername(currentUserName);
		List<ImageMetaData> imageMetaDataList = imageMetaDataRepository.findByOwner(appUser);
		return toListOfImageCommand(imageMetaDataList);
	}

	private List<ImageCommand> toListOfImageCommand(List<ImageMetaData> imageMetaDataList) {
		if (imageMetaDataList.isEmpty()) {
			return Collections.emptyList();
		}

		return imageMetaDataList.stream().map(imageMetaData -> toImageCommand(imageMetaData)).collect(Collectors.toList());
	}

	private ImageCommand toImageCommand(ImageMetaData imageMetaData) {
		ImageCommand imageCommand = new ImageCommand();
		imageCommand.setDescription(imageMetaData.getDescription());
		imageCommand.setGalleryGroup(imageMetaData.getImageGroup().getName());
		imageCommand.setImageId(imageMetaData.getId());
		return imageCommand;
	}

	public BinaryImage loadImageById(Long imageId) {
		ImageMetaData imageMetaData = imageMetaDataRepository.findOne(imageId);
		if (imageMetaData == null) {
			return null;
		}

		BinaryImage imageData = imageLocationService.getImageByKey(imageMetaData.getImageKey(), imageMetaData.getImageGroup().getName());
		return imageData;
	}

	public BinaryImage loadThumbnailById(Long imageId) {
		ImageMetaData imageMetaData = imageMetaDataRepository.findOne(imageId);
		if (imageMetaData == null) {
			return null;
		}

		BinaryImage imageData = imageLocationService.getThumbnailByKey(imageMetaData.getImageKey(),
				imageMetaData.getImageGroup().getName());
		return imageData;
	}

	public void deleteImageById(Long imageId) {
		ImageMetaData imageMetaData = imageMetaDataRepository.findOne(imageId);
		if (imageMetaData == null) {
			LOG.warn("Could not found image with id: {}", imageId);
			return;
		}

		imageMetaDataRepository.delete(imageId);
		imageLocationService.deleteImage(imageMetaData.getImageKey(), imageMetaData.getImageGroup().getName());
	}

	public boolean isImageOfUser(Long imageId, AppUser appUser) {
		ImageMetaData imageMetaData = imageMetaDataRepository.findOne(imageId);
		if (imageMetaData == null) {
			return false;
		}
		
		if (imageMetaData.getOwner().getUsername().equals(appUser.getUsername())) {
			return true;
		}
		
		return false;
	}

	public List<String> findAvailableImageGroups() {
		return imageGroupRepository.findAll().stream().map(imageGroup -> imageGroup.getName()).sorted().collect(Collectors.toList());
	}

	public Long loadImageOfUser(String currentUserName, String galleryName) {
		
		
		// TODO Auto-generated method stub
		return null;
	}
}
