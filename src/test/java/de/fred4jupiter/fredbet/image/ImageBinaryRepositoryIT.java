package de.fred4jupiter.fredbet.image;

import de.fred4jupiter.fredbet.common.TransactionalIntegrationTest;
import de.fred4jupiter.fredbet.domain.entity.ImageBinary;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@TransactionalIntegrationTest
public class ImageBinaryRepositoryIT {

    private static final Logger LOG = LoggerFactory.getLogger(ImageBinaryRepositoryIT.class);

    @Autowired
    private ImageBinaryRepository imageBinaryRepository;

    @Test
    public void saveImageInDatabase() throws IOException {
        byte[] fileAsByteArray = FileUtils.readFileToByteArray(new File("src/test/resources/sample_images/kitten.jpg"));
        assertThat(fileAsByteArray).isNotNull();

        final String imageKey = imageBinaryRepository.saveImage(fileAsByteArray, fileAsByteArray);
        assertThat(imageKey).isNotBlank();

        ImageBinary retrievedFromDb = imageBinaryRepository.getReferenceById(imageKey);
        assertThat(retrievedFromDb).isNotNull();
        assertThat(retrievedFromDb.getKey()).isNotNull();
        assertThat(retrievedFromDb.getKey()).isEqualTo(imageKey);
        assertThat(retrievedFromDb.getImageBinary()).isNotNull();
        assertThat(retrievedFromDb.getThumbImageBinary()).isNotNull();

        String tempDir = System.getProperty("java.io.tmpdir");

        File targetFile = new File(tempDir + File.separator + "kitten_from_db.jpg");
        FileUtils.writeByteArrayToFile(targetFile, retrievedFromDb.getImageBinary());
        LOG.debug("written file to: {}", targetFile);
        assertThat(targetFile.exists()).isTrue();
    }
}
