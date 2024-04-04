package de.fred4jupiter.fredbet.config;

import de.fred4jupiter.fredbet.props.FredbetProperties;
import de.fred4jupiter.fredbet.props.ImageLocation;
import de.fred4jupiter.fredbet.repository.ImageBinaryRepository;
import de.fred4jupiter.fredbet.service.image.storage.DatabaseImageLocationStrategy;
import de.fred4jupiter.fredbet.service.image.storage.FilesystemImageLocationStrategy;
import de.fred4jupiter.fredbet.service.image.storage.ImageLocationStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ImageLocationConfig {

    private static final Logger LOG = LoggerFactory.getLogger(ImageLocationConfig.class);

    @Bean
    public ImageLocationStrategy imageLocationStrategy(FredbetProperties fredbetProperties, ImageBinaryRepository imageBinaryRepository) {
        final ImageLocation imageLocation = fredbetProperties.imageLocation();

        if (ImageLocation.FILE_SYSTEM.equals(imageLocation)) {
            LOG.info("Storing images in filesystem.");
            return new FilesystemImageLocationStrategy(fredbetProperties.imageFileSystemBaseFolder());
        }
        LOG.info("Storing images in database.");
        return new DatabaseImageLocationStrategy(imageBinaryRepository);
    }
}
