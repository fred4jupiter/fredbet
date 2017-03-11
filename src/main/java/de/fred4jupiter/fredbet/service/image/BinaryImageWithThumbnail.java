package de.fred4jupiter.fredbet.service.image;

public class BinaryImageWithThumbnail {

	private String key;

	private byte[] imageBinary;

	private byte[] thumbImageBinary;

	public BinaryImageWithThumbnail(String key, byte[] imageBinary, byte[] thumbImageBinary) {
		super();
		this.key = key;
		this.imageBinary = imageBinary;
		this.thumbImageBinary = thumbImageBinary;
	}

	public String getKey() {
		return key;
	}

	public byte[] getImageBinary() {
		return imageBinary;
	}

	public byte[] getThumbImageBinary() {
		return thumbImageBinary;
	}

}
