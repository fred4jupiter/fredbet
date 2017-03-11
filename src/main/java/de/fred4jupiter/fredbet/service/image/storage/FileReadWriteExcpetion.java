package de.fred4jupiter.fredbet.service.image.storage;

public class FileReadWriteExcpetion extends RuntimeException {

	private static final long serialVersionUID = -76957297993775394L;

	public FileReadWriteExcpetion(String message) {
		super(message);
	}

	public FileReadWriteExcpetion(String message, Throwable cause) {
		super(message, cause);
	}

}
