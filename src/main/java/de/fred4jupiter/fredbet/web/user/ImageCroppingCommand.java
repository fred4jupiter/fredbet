package de.fred4jupiter.fredbet.web.user;

import org.springframework.web.multipart.MultipartFile;

public class ImageCroppingCommand {

	private Long imageId;

	private MultipartFile newUserImageFile;

	public MultipartFile getNewUserImageFile() {
		return newUserImageFile;
	}

	public void setNewUserImageFile(MultipartFile newUserImageFile) {
		this.newUserImageFile = newUserImageFile;
	}

	public Long getImageId() {
		return imageId;
	}

	public void setImageId(Long imageId) {
		this.imageId = imageId;
	}

}
