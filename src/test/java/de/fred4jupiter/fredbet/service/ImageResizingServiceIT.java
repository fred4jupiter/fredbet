package de.fred4jupiter.fredbet.service;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

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
	public void resizeImageWithByteArray() throws IOException {
		File file = new File("src/test/resources/sample_images/sampeImage.jpg");
		assertNotNull(file);
		assertTrue(file.exists());

		byte[] thumbByteArray = imageResizingService.createThumbnail(FileUtils.readFileToByteArray(file));

		String tempDir = System.getProperty("java.io.tmpdir");
		File outputFile = new File(tempDir + File.separator + file.getName());

		FileUtils.writeByteArrayToFile(outputFile, thumbByteArray);
		log.debug("written file to: {}", outputFile);
		assertTrue(outputFile.exists());
	}
}
