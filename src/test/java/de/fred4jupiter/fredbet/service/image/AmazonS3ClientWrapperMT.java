package de.fred4jupiter.fredbet.service.image;

import java.io.IOException;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import de.fred4jupiter.fredbet.AbstractIntegrationTest;

public class AmazonS3ClientWrapperMT extends AbstractIntegrationTest{

    @Autowired
    private AmazonS3ClientWrapper amazonS3ClientWrapper;
    
    @Test
    public void uploadFile() throws IOException {
        String someString = "Hello World";
        amazonS3ClientWrapper.uploadFile("demoFile.txt", someString.getBytes(), "text/plain");
    }

}
