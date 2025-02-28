package de.fred4jupiter.fredbet.repository;

import de.fred4jupiter.fredbet.common.TransactionalIntegrationTest;
import de.fred4jupiter.fredbet.domain.entity.ImageBinary;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@TransactionalIntegrationTest
public class ImageBinaryRepositoryIT {

    private static final Logger LOG = LoggerFactory.getLogger(ImageBinaryRepositoryIT.class);

    @Autowired
    private ImageBinaryRepository imageBinaryRepository;

    @Test
    public void saveImageInDatabase() throws IOException {
        byte[] fileAsByteArray = FileUtils.readFileToByteArray(new File("src/test/resources/sample_images/kitten.jpg"));
        assertNotNull(fileAsByteArray);

        final String key = UUID.randomUUID().toString();
        ImageBinary imageBinary = new ImageBinary(key, fileAsByteArray, 1L, fileAsByteArray);

        ImageBinary saved = imageBinaryRepository.save(imageBinary);
        assertNotNull(saved);
        assertEquals(key, saved.getKey());

        ImageBinary retrievedFromDb = imageBinaryRepository.getReferenceById(saved.getKey());
        assertNotNull(retrievedFromDb);
        assertEquals(saved.getKey(), retrievedFromDb.getKey());
        assertNotNull(retrievedFromDb.getImageBinary());

        String tempDir = System.getProperty("java.io.tmpdir");

        File targetFile = new File(tempDir + File.separator + "kitten_from_db.jpg");
        FileUtils.writeByteArrayToFile(targetFile, retrievedFromDb.getImageBinary());
        LOG.debug("written file to: {}", targetFile);
        assertThat(targetFile.exists()).isTrue();
    }
}
