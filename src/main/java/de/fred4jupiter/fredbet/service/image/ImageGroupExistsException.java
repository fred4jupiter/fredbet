package de.fred4jupiter.fredbet.service.image;

public class ImageGroupExistsException extends RuntimeException {

	private static final long serialVersionUID = -1275028180735278658L;

	public ImageGroupExistsException(String message) {
		super(message);
	}

	public ImageGroupExistsException(Throwable cause) {
		super(cause);
	}

	public ImageGroupExistsException(String message, Throwable cause) {
		super(message, cause);
	}

}
