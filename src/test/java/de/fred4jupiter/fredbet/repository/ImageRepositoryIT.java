package de.fred4jupiter.fredbet.repository;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import de.fred4jupiter.fredbet.AbstractIntegrationTest;
import de.fred4jupiter.fredbet.domain.Image;

public class ImageRepositoryIT extends AbstractIntegrationTest {

	private static final Logger log = LoggerFactory.getLogger(ImageRepositoryIT.class);

	@Autowired
	private ImageRepository imageRepository;

	@Test
	public void saveImageInDatabase() throws IOException {
		byte[] fileAsByteArray = FileUtils.readFileToByteArray(new File("src/test/resources/sample_images/kitten.jpg"));
		assertNotNull(fileAsByteArray);

		Image fileStorage = new Image("kitten.jpg", fileAsByteArray, "sampleGallery", fileAsByteArray);
		Image saved = imageRepository.save(fileStorage);
		assertNotNull(saved);
		assertNotNull(saved.getId());

		Image retrievedFromDb = imageRepository.findOne(saved.getId());
		assertNotNull(retrievedFromDb);
		assertEquals(saved.getId(), retrievedFromDb.getId());
		assertNotNull(retrievedFromDb.getImageBinary());

		String tempDir = System.getProperty("java.io.tmpdir");

		File targetFile = new File(tempDir + File.separator + "kitten_from_db.jpg");
		FileUtils.writeByteArrayToFile(targetFile, retrievedFromDb.getImageBinary());
		log.debug("written file to: {}", targetFile);
		assertTrue(targetFile.exists());
	}
}
