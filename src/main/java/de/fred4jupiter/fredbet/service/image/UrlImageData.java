package de.fred4jupiter.fredbet.service.image;

/**
 * @deprecated not used
 * 
 * @author michael
 *
 */
@Deprecated
public class UrlImageData implements ImageData {

	private String fileUrl;

	private String thumbnailFileUrl;

	private String key;

	public UrlImageData(String fileUrl, String thumbnailFileUrl, String key) {
		super();
		this.fileUrl = fileUrl;
		this.thumbnailFileUrl = thumbnailFileUrl;
		this.key = key;
	}

	@Override
	public String getImageUrl() {
		return fileUrl;
	}

	@Override
	public String getThumbnailUrl() {
		return thumbnailFileUrl;
	}

	@Override
	public byte[] getBinary() {
		throw new IllegalStateException("Image binary not supported by UrlImageData!");
	}

	@Override
	public byte[] getThumbnailBinary() {
		throw new IllegalStateException("Image binary not supported by UrlImageData!");
	}

	@Override
	public String getKey() {
		return key;
	}

}
