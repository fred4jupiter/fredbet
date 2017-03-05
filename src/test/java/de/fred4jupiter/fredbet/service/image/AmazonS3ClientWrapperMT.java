package de.fred4jupiter.fredbet.service.image;

import static org.junit.Assert.*;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import de.fred4jupiter.fredbet.AbstractIntegrationTest;

public class AmazonS3ClientWrapperMT extends AbstractIntegrationTest {

	@Autowired
	private AmazonS3ClientWrapper amazonS3ClientWrapper;

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
	
	
}
