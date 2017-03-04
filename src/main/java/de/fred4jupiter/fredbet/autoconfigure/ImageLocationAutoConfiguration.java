package de.fred4jupiter.fredbet.autoconfigure;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import de.fred4jupiter.fredbet.FredbetProperties;
import de.fred4jupiter.fredbet.repository.ImageBinaryRepository;
import de.fred4jupiter.fredbet.service.image.DatabaseImageLocationService;
import de.fred4jupiter.fredbet.service.image.FilesystemImageLocationService;
import de.fred4jupiter.fredbet.service.image.ImageLocationService;

@Configuration
public class ImageLocationAutoConfiguration {

    private static final Logger LOG = LoggerFactory.getLogger(ImageLocationAutoConfiguration.class);

    @ConditionalOnProperty(prefix = "fredbet", name = "image-location", havingValue = "file-system", matchIfMissing = true)
    @Bean
    public ImageLocationService filesystemImageLocationService(FredbetProperties fredbetProperties) {
        return new FilesystemImageLocationService(fredbetProperties.getImageFileSytemBaseFolder());
    }

    @ConditionalOnProperty(prefix = "fredbet", name = "image-location", havingValue = "database", matchIfMissing = false)
    @Bean
    public ImageLocationService databaseImageLocationService(ImageBinaryRepository imageBinaryRepository) {
        LOG.info("Storing images in database.");
        return new DatabaseImageLocationService(imageBinaryRepository);
    }
}
