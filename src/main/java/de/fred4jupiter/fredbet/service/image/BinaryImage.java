package de.fred4jupiter.fredbet.service.image;

public class BinaryImage {

	private final String key;

	private final byte[] imageBinary;

	public BinaryImage(String key, byte[] imageBinary) {
		super();
		this.key = key;
		this.imageBinary = imageBinary;
	}

	public String getKey() {
		return key;
	}

	public byte[] getImageBinary() {
		return imageBinary;
	}

}
