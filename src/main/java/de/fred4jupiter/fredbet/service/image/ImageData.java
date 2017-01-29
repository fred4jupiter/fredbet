package de.fred4jupiter.fredbet.service.image;

public interface ImageData {

	String getImageUrl();
	
	String getThumbnailUrl();
	
	byte[] getBinary();
	
	byte[] getThumbnailBinary();
	
	String getKey();
}
