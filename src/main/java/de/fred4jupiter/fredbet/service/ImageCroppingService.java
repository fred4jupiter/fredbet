package de.fred4jupiter.fredbet.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.fred4jupiter.fredbet.domain.ImageGroup;
import de.fred4jupiter.fredbet.security.SecurityService;
import de.fred4jupiter.fredbet.service.image.ImageAdministrationService;
import de.fred4jupiter.fredbet.web.image.Rotation;

@Service
public class ImageCroppingService {

	private ImageAdministrationService imageAdministrationService;

	private SecurityService securityService;

	private static final String GALLERY_NAME = "Mitstreiter";

	private final Long userImageGroupId;

	@Autowired
	public ImageCroppingService(ImageAdministrationService imageAdministrationService, SecurityService securityService) {
		this.imageAdministrationService = imageAdministrationService;
		this.securityService = securityService;
		ImageGroup imageGroup = imageAdministrationService.createOrFetchImageGroup(GALLERY_NAME);
		this.userImageGroupId = imageGroup.getId();
	}

	public void saveUserProfileImage(byte[] imageByte) {
		final String currentUserName = securityService.getCurrentUserName();
		imageAdministrationService.saveImage(imageByte, userImageGroupId, currentUserName, Rotation.NONE);
	}

}
