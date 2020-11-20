package de.fred4jupiter.fredbet;

import de.fred4jupiter.fredbet.props.FredbetProperties;
import de.fred4jupiter.fredbet.repository.ImageBinaryRepository;
import de.fred4jupiter.fredbet.service.image.storage.DatabaseImageLocationStrategy;
import de.fred4jupiter.fredbet.service.image.storage.FilesystemImageLocationStrategy;
import de.fred4jupiter.fredbet.service.image.storage.ImageLocationStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Auto configures the beans in repect to their configuration in the config
 * properties.
 *
 * @author michael
 */
@Configuration
public class ImageGalleryConfig {

    private static final String IMAGE_LOCATION = "image-location";

    private static final Logger LOG = LoggerFactory.getLogger(ImageGalleryConfig.class);

    @ConditionalOnProperty(prefix = FredbetProperties.PROPS_PREFIX, name = IMAGE_LOCATION, havingValue = "FILE_SYSTEM", matchIfMissing = false)
    @Bean
    public ImageLocationStrategy filesystemImageLocationService(FredbetProperties fredbetProperties) {
        return new FilesystemImageLocationStrategy(fredbetProperties.getImageFileSystemBaseFolder());
    }

    @ConditionalOnProperty(prefix = FredbetProperties.PROPS_PREFIX, name = IMAGE_LOCATION, havingValue = "DATABASE", matchIfMissing = true)
    @Bean
    public ImageLocationStrategy databaseImageLocationService(ImageBinaryRepository imageBinaryRepository) {
        LOG.info("Storing images in database.");
        return new DatabaseImageLocationStrategy(imageBinaryRepository);
    }
}
