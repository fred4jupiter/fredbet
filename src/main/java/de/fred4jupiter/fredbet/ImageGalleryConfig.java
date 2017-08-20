package de.fred4jupiter.fredbet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.ResourcePatternResolver;

import com.amazonaws.services.s3.AmazonS3;

import de.fred4jupiter.fredbet.props.FredbetProperties;
import de.fred4jupiter.fredbet.repository.ImageBinaryRepository;
import de.fred4jupiter.fredbet.service.image.storage.AmazonS3ClientWrapper;
import de.fred4jupiter.fredbet.service.image.storage.AwsS3ImageLocationStrategy;
import de.fred4jupiter.fredbet.service.image.storage.DatabaseImageLocationStrategy;
import de.fred4jupiter.fredbet.service.image.storage.FilesystemImageLocationStrategy;
import de.fred4jupiter.fredbet.service.image.storage.ImageLocationStrategy;

/**
 * Auto configures the beans in repect to their configuration in the config
 * properties.
 * 
 * @author michael
 *
 */
@Configuration
public class ImageGalleryConfig {

	private static final Logger LOG = LoggerFactory.getLogger(ImageGalleryConfig.class);

	@ConditionalOnProperty(prefix = "fredbet", name = "image-location", havingValue = "file-system", matchIfMissing = true)
	@Bean
	public ImageLocationStrategy filesystemImageLocationService(FredbetProperties fredbetProperties) {
		return new FilesystemImageLocationStrategy(fredbetProperties.getImageFileSystemBaseFolder());
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

	@ConditionalOnProperty(prefix = "fredbet", name = "image-location", havingValue = "aws-s3", matchIfMissing = false)
	@Bean
	public AmazonS3ClientWrapper amazonS3ClientWrapper(AmazonS3 amazonS3, ResourceLoader resourceLoader,
			ResourcePatternResolver resourcePatternResolver, FredbetProperties fredbetProperties) {
		return new AmazonS3ClientWrapper(amazonS3, fredbetProperties.getAwsS3bucketName(), resourceLoader, resourcePatternResolver);
	}

}
