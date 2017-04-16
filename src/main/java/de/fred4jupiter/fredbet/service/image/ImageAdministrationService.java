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

	public void saveImage(byte[] binary, Long imageGroupId, String description, Rotation rotation) {
		final ImageGroup imageGroup = imageGroupRepository.findOne(imageGroupId);

		final String key = imageKeyGenerator.generateKey();

		ImageMetaData image = new ImageMetaData(key, imageGroup, securityService.getCurrentUser());
		image.setDescription(description);
		imageMetaDataRepository.save(image);

		byte[] thumbnail = imageResizingService.createThumbnail(binary, rotation);
		byte[] imageByte = imageResizingService.minimizeToDefaultSize(binary, rotation);

		imageLocationService.saveImage(key, imageGroup.getId(), imageByte, thumbnail);
	}

	public void saveUserProfileImage(byte[] binary, Long imageGroupId) {
		final String key = imageKeyGenerator.generateKey();
		ImageMetaData imageMetaData = securityService.getCurrentUserProfileImageMetaData();
		if (imageMetaData == null) {
			// create new user profile image
			final ImageGroup imageGroup = imageGroupRepository.findOne(imageGroupId);
			final AppUser appUser = appUserRepository.findOne(securityService.getCurrentUser().getId());
			imageMetaData = new ImageMetaData(key, imageGroup, appUser);
			imageMetaData.setDescription(appUser.getUsername());
		} else {
			imageMetaData.setImageKey(key);
		}

		imageMetaDataRepository.save(imageMetaData);

		byte[] thumbnail = imageResizingService.createThumbnail(binary, Rotation.NONE);
		byte[] imageByte = imageResizingService.minimizeToDefaultSize(binary, Rotation.NONE);
		imageLocationService.saveImage(key, imageMetaData.getImageGroup().getId(), imageByte, thumbnail);
	}

	public ImageGroup createOrFetchImageGroup(String galleryGroupName) {
		ImageGroup imageGroup = imageGroupRepository.findByName(galleryGroupName);

		if (imageGroup == null) {
			imageGroup = new ImageGroup(galleryGroupName);
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
		imageCommand.setImageId(imageMetaData.getId());
		imageCommand.setImageKey(imageMetaData.getImageKey());
		imageCommand.setDescription(imageMetaData.getDescription());
		imageCommand.setGalleryGroup(imageMetaData.getImageGroup().getName());
		return imageCommand;
	}

	public BinaryImage loadImageByImageKey(String imageKey) {
		ImageMetaData imageMetaData = imageMetaDataRepository.findByImageKey(imageKey);
		if (imageMetaData == null) {
			return null;
		}

		return imageLocationService.getImageByKey(imageMetaData.getImageKey(), imageMetaData.getImageGroup().getId());
	}

	public BinaryImage loadThumbnailByImageKey(String imageKey) {
		ImageMetaData imageMetaData = imageMetaDataRepository.findByImageKey(imageKey);
		if (imageMetaData == null) {
			return null;
		}

		return imageLocationService.getThumbnailByKey(imageMetaData.getImageKey(), imageMetaData.getImageGroup().getId());
	}

	public void deleteImageByImageKey(String imageKey) {
		ImageMetaData imageMetaData = imageMetaDataRepository.findByImageKey(imageKey);
		if (imageMetaData == null) {
			LOG.warn("Could not found image with imageKey: {}", imageKey);
			return;
		}

		imageMetaDataRepository.delete(imageMetaData);
		imageLocationService.deleteImage(imageMetaData.getImageKey(), imageMetaData.getImageGroup().getId());
	}

	public boolean isImageOfUser(String imageKey, AppUser appUser) {
		ImageMetaData imageMetaData = imageMetaDataRepository.findByImageKey(imageKey);
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
}
