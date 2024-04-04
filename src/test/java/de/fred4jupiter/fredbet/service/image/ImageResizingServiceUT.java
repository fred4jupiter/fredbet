package de.fred4jupiter.fredbet.service.image;

import de.fred4jupiter.fredbet.common.UnitTest;
import de.fred4jupiter.fredbet.props.FredbetProperties;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@UnitTest
public class ImageResizingServiceUT {

    private static final Logger LOG = LoggerFactory.getLogger(ImageResizingServiceUT.class);

    @InjectMocks
    private ImageResizingService imageResizingService;

    @Mock
    private FredbetProperties fredbetProperties;

    @Test
    public void createThumbnail() throws IOException {
        final Integer thumbailSize = 75;
        when(fredbetProperties.thumbnailSize()).thenReturn(thumbailSize);

        File file = new File("src/test/resources/sample_images/sampleImage_800.jpg");
        assertNotNull(file);
        assertTrue(file.exists());

        byte[] thumbByteArray = imageResizingService.createThumbnail(FileUtils.readFileToByteArray(file));
        assertNotNull(thumbByteArray);

        File outputFile = createOutputFile(file);

        FileUtils.writeByteArrayToFile(outputFile, thumbByteArray);
        LOG.debug("written file to: {}", outputFile);
        assertTrue(outputFile.exists());

        BufferedImage bufferedImage = ImageIO.read(outputFile);
        assertThat(bufferedImage.getWidth()).isEqualTo(thumbailSize);
    }

    private File createOutputFile(File file) {
        String tempDir = System.getProperty("java.io.tmpdir");
        return new File(tempDir + File.separator + file.getName());
    }
}
