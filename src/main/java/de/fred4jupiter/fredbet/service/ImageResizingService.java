package de.fred4jupiter.fredbet.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import net.coobird.thumbnailator.Thumbnails;

@Service
public class ImageResizingService {

	// TODO: make configurable
	private static final int THUMBNAIL_SIZE = 80;
	
	private static final int DEFAULT_MAX_SIZE = 1920;
	
	private static final Logger log = LoggerFactory.getLogger(ImageResizingService.class);

	public byte[] createThumbnail(byte[] imageBinary) {
		return createImageOfSize(imageBinary, THUMBNAIL_SIZE);
	}
	
	public byte[] createDefaultSizedImage(byte[] imageBinary) {
		return createImageOfSize(imageBinary, DEFAULT_MAX_SIZE);
	}
	
	public byte[] createImageOfSize(byte[] imageBinary, int size) {
		try (ByteArrayInputStream byteIn = new ByteArrayInputStream(imageBinary);
				ByteArrayOutputStream byteOut = new ByteArrayOutputStream()) {
			Thumbnails.of(byteIn).size(size, size).toOutputStream(byteOut);
			return byteOut.toByteArray();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return null;
		}
	}
}
