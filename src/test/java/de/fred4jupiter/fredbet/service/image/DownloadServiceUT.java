package de.fred4jupiter.fredbet.service.image;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fred4jupiter.fredbet.domain.Image;
import de.fred4jupiter.fredbet.domain.ImageGroup;
import de.fred4jupiter.fredbet.service.image.DownloadService;

@RunWith(MockitoJUnitRunner.class)
public class DownloadServiceUT {

	private static final Logger log = LoggerFactory.getLogger(DownloadServiceUT.class);

	@InjectMocks
	private DownloadService downloadService;

	@Mock
	private Image image;

	@Mock
	private ImageGroup imageGroup;

	@Test
	public void compressFile() throws IOException {
		File file = new File("src/test/resources/sample_images/sampleImage_800.jpg");
		assertNotNull(file);
		assertTrue(file.exists());

		byte[] imageAsByteArray = FileUtils.readFileToByteArray(file);

		when(image.getImageBinary()).thenReturn(imageAsByteArray);
		when(image.getImageGroup()).thenReturn(imageGroup);
		when(imageGroup.getName()).thenReturn("Gallery 1");

		byte[] zipFileAsByteArray = downloadService.compressToZipFile(Arrays.asList(image));
		assertNotNull(zipFileAsByteArray);

		File outputFile = createOutputFile(new File("result.zip"));

		FileUtils.writeByteArrayToFile(outputFile, zipFileAsByteArray);
		log.debug("written file to: {}", outputFile);
		assertTrue(outputFile.exists());
	}

	private File createOutputFile(File file) {
		String tempDir = System.getProperty("java.io.tmpdir");
		return new File(tempDir + File.separator + file.getName());
	}
}
