package de.fred4jupiter.fredbet.web.image;

import org.springframework.web.multipart.MultipartFile;

public class ImageUploadCommand {

	private String galleryGroup;

	private String description;

	private MultipartFile myFile;

	private Rotation rotation = Rotation.NONE;

	public MultipartFile getMyFile() {
		return myFile;
	}

	public void setMyFile(MultipartFile myFile) {
		this.myFile = myFile;
	}

	public String getGalleryGroup() {
		return galleryGroup;
	}

	public void setGalleryGroup(String galleryGroup) {
		this.galleryGroup = galleryGroup;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Rotation getRotation() {
		return rotation;
	}

	public void setRotation(Rotation rotation) {
		this.rotation = rotation;
	}

}
