package de.fred4jupiter.fredbet.service.image;

import de.fred4jupiter.fredbet.UnitTest;
import de.fred4jupiter.fredbet.props.FredbetProperties;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@UnitTest
public class ImageResizingServiceUT {

    private static final int IMAGE_SIZE = 1920;

    private static final int THUMB_SIZE = 75;

    private static final Logger log = LoggerFactory.getLogger(ImageResizingServiceUT.class);

    private ImageResizingService imageResizingService;

    @Mock
    private FredbetProperties fredbetProperties;

    @BeforeEach
    public void setUp() {
        when(fredbetProperties.getThumbnailSize()).thenReturn(THUMB_SIZE);
        when(fredbetProperties.getImageSize()).thenReturn(IMAGE_SIZE);

        this.imageResizingService = new ImageResizingService(fredbetProperties);
    }

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

    @Test
    public void createDefaultSizedImageNoSizeReduction() throws IOException {
        File file = new File("src/test/resources/sample_images/sampleImage_800.jpg");
        assertNotNull(file);
        assertTrue(file.exists());

        byte[] thumbByteArray = imageResizingService.minimizeToDefaultSize(FileUtils.readFileToByteArray(file));
        assertNotNull(thumbByteArray);

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
        assertNotNull(thumbByteArray);

        File outputFile = createOutputFile(file);
        assertNotNull(outputFile);

        FileUtils.writeByteArrayToFile(outputFile, thumbByteArray);
        log.debug("written file to: {}", outputFile);
        assertTrue(outputFile.exists());

        BufferedImage bufferedImage = ImageIO.read(outputFile);
        assertEquals(IMAGE_SIZE, bufferedImage.getWidth());
    }

    private File createOutputFile(File file) {
        String tempDir = System.getProperty("java.io.tmpdir");
        File outputFile = new File(tempDir + File.separator + file.getName());
        return outputFile;
    }
}
