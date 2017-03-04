package de.fred4jupiter.fredbet.autoconfigure;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import de.fred4jupiter.fredbet.FredbetProperties;
import de.fred4jupiter.fredbet.repository.ImageBinaryRepository;
import de.fred4jupiter.fredbet.service.image.AmazonS3ClientWrapper;
import de.fred4jupiter.fredbet.service.image.AwsS3ImageLocationStrategy;
import de.fred4jupiter.fredbet.service.image.DatabaseImageLocationStrategy;
import de.fred4jupiter.fredbet.service.image.FilesystemImageLocationStrategy;
import de.fred4jupiter.fredbet.service.image.ImageLocationStrategy;

/**
 * Auto configures the beans in repect to their configuration in the config properties.
 * 
 * @author michael
 *
 */
@Configuration
public class ImageLocationAutoConfiguration {

    private static final Logger LOG = LoggerFactory.getLogger(ImageLocationAutoConfiguration.class);

    @ConditionalOnProperty(prefix = "fredbet", name = "image-location", havingValue = "file-system", matchIfMissing = true)
    @Bean
    public ImageLocationStrategy filesystemImageLocationService(FredbetProperties fredbetProperties) {
        return new FilesystemImageLocationStrategy(fredbetProperties.getImageFileSytemBaseFolder());
    }

    @ConditionalOnProperty(prefix = "fredbet", name = "image-location", havingValue = "database", matchIfMissing = false)
    @Bean
    public ImageLocationStrategy databaseImageLocationService(ImageBinaryRepository imageBinaryRepository) {
        LOG.info("Storing images in database.");
        return new DatabaseImageLocationStrategy(imageBinaryRepository);
    }
    
    @ConditionalOnProperty(prefix = "fredbet", name = "image-location", havingValue = "aws-s3", matchIfMissing = false)
    @Bean
    public ImageLocationStrategy awsS3ImageLocationStrategy(AmazonS3ClientWrapper amazonS3ClientWrapper) {
        LOG.info("Storing images in AWS S3.");
        return new AwsS3ImageLocationStrategy(amazonS3ClientWrapper);
    }
}
