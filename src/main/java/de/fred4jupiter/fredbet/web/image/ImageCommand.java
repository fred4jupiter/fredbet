package de.fred4jupiter.fredbet.web.image;

public class ImageCommand {

	private Long imageId;

	private String galleryGroup;

	private String description;

	public Long getImageId() {
		return imageId;
	}

	public void setImageId(Long imageId) {
		this.imageId = imageId;
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
}
