package de.fred4jupiter.fredbet.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import net.coobird.thumbnailator.Thumbnails;

@Service
public class ImageResizingService {

	private static final int THUMBNAIL_SIZE = 80;
	
	private static final Logger log = LoggerFactory.getLogger(ImageResizingService.class);

	public byte[] createThumbnail(byte[] imageBinary) {
		try (ByteArrayInputStream byteIn = new ByteArrayInputStream(imageBinary);
				ByteArrayOutputStream byteOut = new ByteArrayOutputStream()) {
			Thumbnails.of(byteIn).size(THUMBNAIL_SIZE, THUMBNAIL_SIZE).toOutputStream(byteOut);
			return byteOut.toByteArray();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return null;
		}
	}
	
	public void createThumbnail(File inputFile, File outputFile) {
		try {
			Thumbnails.of(inputFile).size(THUMBNAIL_SIZE, THUMBNAIL_SIZE).toFile(outputFile);
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		}
	}
}
