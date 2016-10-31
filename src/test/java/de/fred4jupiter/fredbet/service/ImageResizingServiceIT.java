package de.fred4jupiter.fredbet.service;

import static org.junit.Assert.*;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RunWith(MockitoJUnitRunner.class)
public class ImageResizingServiceIT {

	private static final Logger log = LoggerFactory.getLogger(ImageResizingServiceIT.class);

	@InjectMocks
	private ImageResizingService imageResizingService;

	@Test
	public void createThumbnail() throws IOException {
		File file = new File("src/test/resources/sample_images/sampleImage_800.jpg");
		assertNotNull(file);
		assertTrue(file.exists());

		byte[] thumbByteArray = imageResizingService.createThumbnail(FileUtils.readFileToByteArray(file));

		File outputFile = createOutputFile(file);

		FileUtils.writeByteArrayToFile(outputFile, thumbByteArray);
		log.debug("written file to: {}", outputFile);
		assertTrue(outputFile.exists());
		
		BufferedImage bufferedImage = ImageIO.read(outputFile);
		assertEquals(40, bufferedImage.getWidth());
	}
	
	@Test
	public void createDefaultSizedImageNoSizeReduction() throws IOException {
		File file = new File("src/test/resources/sample_images/sampleImage_800.jpg");
		assertNotNull(file);
		assertTrue(file.exists());

		byte[] thumbByteArray = imageResizingService.minimizeToDefaultSize(FileUtils.readFileToByteArray(file));

		File outputFile = createOutputFile(file);

		FileUtils.writeByteArrayToFile(outputFile, thumbByteArray);
		log.debug("written file to: {}", outputFile);
		assertTrue(outputFile.exists());
		
		BufferedImage bufferedImage = ImageIO.read(outputFile);
		assertEquals(800, bufferedImage.getWidth());
	}
	
	@Test
	public void createDefaultSizedImage() throws IOException {
		File file = new File("src/test/resources/sample_images/sampleImage_2560.jpg");
		assertNotNull(file);
		assertTrue(file.exists());

		byte[] thumbByteArray = imageResizingService.minimizeToDefaultSize(FileUtils.readFileToByteArray(file));

		File outputFile = createOutputFile(file);

		FileUtils.writeByteArrayToFile(outputFile, thumbByteArray);
		log.debug("written file to: {}", outputFile);
		assertTrue(outputFile.exists());
		
		BufferedImage bufferedImage = ImageIO.read(outputFile);
		assertEquals(1920, bufferedImage.getWidth());
	}

	private File createOutputFile(File file) {
		String tempDir = System.getProperty("java.io.tmpdir");
		File outputFile = new File(tempDir + File.separator + file.getName());
		return outputFile;
	}
}
