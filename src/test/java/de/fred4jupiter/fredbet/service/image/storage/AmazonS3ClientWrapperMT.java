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

import de.fred4jupiter.fredbet.service.image.BinaryImage;

public class AmazonS3ClientWrapperMT {

	private static final Logger LOG = LoggerFactory.getLogger(AmazonS3ClientWrapperMT.class);

	private AmazonS3ClientWrapper amazonS3ClientWrapper;

	@Before
	public void setup() {
		// we are using the ~/.aws/credetials in this case
		String region = "eu-central-1";
		String bucketName = "fredbet";
		this.amazonS3ClientWrapper = new AmazonS3ClientWrapper(null, null, region, bucketName);
	}

	@Test
	public void uploadFile() {
		final String someString = "Hello World";
		final String key = "demoFile.txt";
		amazonS3ClientWrapper.uploadTextFile(key, someString.getBytes());
	}

	@Test
	public void uploadAndDownloadFile() {
		final String someString = "Hello World";
		final String key = "demoFile.txt";
		amazonS3ClientWrapper.uploadTextFile(key, someString.getBytes());

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
	public void listFiles() {
		List<String> listFiles = amazonS3ClientWrapper.listFiles();
		listFiles.stream().forEach(key -> LOG.info("file: {}", key));
	}

	@Test
	public void uploadImagesAndDownloadAll() throws IOException {
		byte[] fileAsByteArray = FileUtils.readFileToByteArray(new File("src/test/resources/sample_images/kitten.jpg"));
		assertNotNull(fileAsByteArray);

		final String key1 = "1/IM_kitten1.jpg";
		final String key2 = "2/IM_kitten2.jpg";
		 amazonS3ClientWrapper.uploadImageFile(key1, fileAsByteArray);
		 amazonS3ClientWrapper.uploadImageFile(key2, fileAsByteArray);

		List<String> listFiles = amazonS3ClientWrapper.listFiles(".jpg");

		List<BinaryImage> files = amazonS3ClientWrapper.downloadAllFiles(listFiles);
		assertFalse(files.isEmpty());
		assertEquals(2, files.size());
		for (BinaryImage binaryImage : files) {
			String key = binaryImage.getKey();
			assertNotNull(key);
			LOG.debug("key: {}", key);
			File file = new File("d://Temp1/" + key);
			FileUtils.writeByteArrayToFile(file, binaryImage.getImageBinary());
			assertTrue(file.exists());
		}
	}

}
