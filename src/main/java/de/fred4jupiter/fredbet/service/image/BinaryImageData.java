package de.fred4jupiter.fredbet.service.image;

public class BinaryImageData implements ImageData {

	private String key;

	private byte[] imageBinary;

	private byte[] thumbImageBinary;

	public BinaryImageData(String key, byte[] imageBinary, byte[] thumbImageBinary) {
		super();
		this.key = key;
		this.imageBinary = imageBinary;
		this.thumbImageBinary = thumbImageBinary;
	}

	@Override
	public String getImageUrl() {
		throw new IllegalStateException("Image URL not supported by UrlImageData!");
	}

	@Override
	public String getThumbnailUrl() {
		throw new IllegalStateException("Image thumbnail URL not supported by UrlImageData!");
	}

	@Override
	public byte[] getBinary() {
		return imageBinary;
	}

	@Override
	public byte[] getThumbnailBinary() {
		return thumbImageBinary;
	}

	@Override
	public String getKey() {
		return key;
	}

}
