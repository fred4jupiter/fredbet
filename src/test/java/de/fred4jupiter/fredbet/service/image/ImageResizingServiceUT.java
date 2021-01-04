package de.fred4jupiter.fredbet.service.image;

import de.fred4jupiter.fredbet.UnitTest;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

@UnitTest
public class ImageResizingServiceUT {

    private static final int THUMB_SIZE = ImageResizingService.THUMBNAIL_SIZE;

    private static final Logger log = LoggerFactory.getLogger(ImageResizingServiceUT.class);

    @InjectMocks
    private ImageResizingService imageResizingService;

    @Test
    public void createThumbnail() throws IOException {
        File file = new File("src/test/resources/sample_images/sampleImage_800.jpg");
        assertNotNull(file);
        assertTrue(file.exists());

        byte[] thumbByteArray = imageResizingService.createThumbnail(FileUtils.readFileToByteArray(file));
        assertNotNull(thumbByteArray);

        File outputFile = createOutputFile(file);

        FileUtils.writeByteArrayToFile(outputFile, thumbByteArray);
        log.debug("written file to: {}", outputFile);
        assertTrue(outputFile.exists());

        BufferedImage bufferedImage = ImageIO.read(outputFile);
        assertEquals(THUMB_SIZE, bufferedImage.getWidth());
    }

    private File createOutputFile(File file) {
        String tempDir = System.getProperty("java.io.tmpdir");
        File outputFile = new File(tempDir + File.separator + file.getName());
        return outputFile;
    }
}
