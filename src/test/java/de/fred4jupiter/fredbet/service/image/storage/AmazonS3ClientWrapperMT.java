package de.fred4jupiter.fredbet.service.image.storage;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AmazonS3ClientWrapperMT {

	private static final Logger LOG = LoggerFactory.getLogger(AmazonS3ClientWrapperMT.class);

	private AmazonS3ClientWrapper amazonS3ClientWrapper;

	@Before
	public void setup() {
		String accessKey = "XXX";
		String secretKey = "XXX";
		String region = "eu-central-1";
		String bucketName = "fredbet";
		this.amazonS3ClientWrapper = new AmazonS3ClientWrapper(accessKey, secretKey, region, bucketName);
	}

	@Test
	public void uploadFile() {
		final String someString = "Hello World";
		final String key = "demoFile.txt";
		amazonS3ClientWrapper.uploadFile(key, someString.getBytes(), "text/plain");
	}

	@Test
	public void uploadAndDownloadFile() {
		final String someString = "Hello World";
		final String key = "demoFile.txt";
		amazonS3ClientWrapper.uploadFile(key, someString.getBytes(), "text/plain");

		byte[] fileContent = amazonS3ClientWrapper.downloadFile(key);
		String downloaded = new String(fileContent);
		assertEquals(someString, downloaded);
	}

	@Test
	public void loadImageAtKey() throws IOException {
		String fileName = "Misc" + "/" + "IM_7a587208-5193-48fe-8168-74519f056be5.jpg";
		byte[] fileBinary = amazonS3ClientWrapper.downloadFile(fileName);
		assertNotNull(fileBinary);
		File file = new File("d://Temp1/" + fileName);
		FileUtils.writeByteArrayToFile(file, fileBinary);
		assertTrue(file.exists());
	}

	@Test
	public void downloadAllFiles() throws IOException {
		byte[] fileAsByteArray = FileUtils.readFileToByteArray(new File("src/test/resources/sample_images/kitten.jpg"));
		assertNotNull(fileAsByteArray);

		amazonS3ClientWrapper.uploadFile("IM_kitten1.jpg", fileAsByteArray, "image/jpeg");
		amazonS3ClientWrapper.uploadFile("IM_kitten2.jpg", fileAsByteArray, "image/jpeg");

		List<File> files = amazonS3ClientWrapper.readAllImagesInBucketWithPrefix("IM");
		assertFalse(files.isEmpty());
		assertEquals(2, files.size());
		for (File file : files) {
			String filename = file.getName();
			assertNotNull(filename);
			LOG.debug("filename: {}", filename);
		}
	}

}
