package de.fred4jupiter.fredbet.config;

import de.fred4jupiter.fredbet.props.FredbetProperties;
import de.fred4jupiter.fredbet.props.ImageLocation;
import de.fred4jupiter.fredbet.repository.ImageBinaryRepository;
import de.fred4jupiter.fredbet.service.image.storage.DatabaseImageLocationStrategy;
import de.fred4jupiter.fredbet.service.image.storage.FilesystemImageLocationStrategy;
import de.fred4jupiter.fredbet.service.image.storage.ImageLocationStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.stereotype.Component;

@Component
public class ImageLocationStrategyFactory implements FactoryBean<ImageLocationStrategy> {

    private static final Logger LOG = LoggerFactory.getLogger(ImageLocationStrategyFactory.class);

    private final ImageLocationStrategy imageLocationStrategy;

    public ImageLocationStrategyFactory(FredbetProperties fredbetProperties, ImageBinaryRepository imageBinaryRepository) {
        final ImageLocation imageLocation = fredbetProperties.getImageLocation();

        if (ImageLocation.FILE_SYSTEM.equals(imageLocation)) {
            LOG.info("Storing images in filesystem.");
            this.imageLocationStrategy = new FilesystemImageLocationStrategy(fredbetProperties.getImageFileSystemBaseFolder());
            return;
        }
        LOG.info("Storing images in database.");
        this.imageLocationStrategy = new DatabaseImageLocationStrategy(imageBinaryRepository);
    }

    @Override
    public ImageLocationStrategy getObject() {
        return this.imageLocationStrategy;
    }

    @Override
    public Class<?> getObjectType() {
        return ImageLocationStrategy.class;
    }
}
