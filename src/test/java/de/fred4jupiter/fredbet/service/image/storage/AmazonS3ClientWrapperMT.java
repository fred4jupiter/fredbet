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
import org.springframework.core.io.Resource;

public class AmazonS3ClientWrapperMT {

	private AmazonS3ClientWrapper amazonS3ClientWrapper;

	@Before
	public void setup() {
		String accessKey = "";
		String secretKey = "";
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
	public void downloadFile() {
		final String someString = "Hello World";
		final String key = "demoFile.txt";
		amazonS3ClientWrapper.uploadFile(key, someString.getBytes(), "text/plain");

		byte[] fileContent = amazonS3ClientWrapper.downloadFile(key);
		String downloaded = new String(fileContent);
		assertEquals(someString, downloaded);
	}
	//
	// @Test
	// public void loadImageAtKey() throws IOException {
	// String fileName = "Misc" + "/" +
	// "IM_7a587208-5193-48fe-8168-74519f056be5.jpg";
	// byte[] fileBinary = amazonS3ClientWrapper.downloadFile(fileName);
	// assertNotNull(fileBinary);
	// File file = new File("d://Temp1/" + fileName);
	// FileUtils.writeByteArrayToFile(file, fileBinary);
	// assertTrue(file.exists());
	// }
	//
	// @Test
	// public void downloadAllFiles() {
	// List<Resource> resources =
	// amazonS3ClientWrapper.readAllImagesInBucketWithPrefix("IM");
	// assertFalse(resources.isEmpty());
	// for (Resource resource : resources) {
	// String filename = resource.getFilename();
	// assertNotNull(filename);
	// LOG.debug("filename: {}", filename);
	// }
	// }

}
