package de.fred4jupiter.fredbet.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.fred4jupiter.fredbet.domain.ImageGroup;
import de.fred4jupiter.fredbet.service.image.ImageAdministrationService;

@Service
public class ImageCroppingService {

	private ImageAdministrationService imageAdministrationService;

	private static final String GALLERY_NAME = "Mitstreiter";

	private final Long userImageGroupId;

	@Autowired
	public ImageCroppingService(ImageAdministrationService imageAdministrationService) {
		this.imageAdministrationService = imageAdministrationService;

		ImageGroup imageGroup = imageAdministrationService.createOrFetchImageGroup(GALLERY_NAME);
		this.userImageGroupId = imageGroup.getId();
	}

	public void saveUserProfileImage(byte[] imageByte) {
		imageAdministrationService.saveUserProfileImage(imageByte, userImageGroupId);
	}

	/**
	 * Image groupd ID of the user profile image group.
	 * 
	 * @return
	 */
	public Long getUserImageGroupId() {
		return userImageGroupId;
	}

}
