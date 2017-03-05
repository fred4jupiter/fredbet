package de.fred4jupiter.fredbet;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.ResourcePatternResolver;

import com.amazonaws.services.s3.AmazonS3;

import de.fred4jupiter.fredbet.service.image.AmazonS3ClientWrapper;

@Configuration
@ConditionalOnProperty(prefix = "fredbet", name = "image-location", havingValue = "aws-s3")
public class AwsConfiguration {

	@Bean
	public AmazonS3ClientWrapper amazonS3ClientWrapper(AmazonS3 amazonS3, FredbetProperties fredbetProperties, ResourceLoader resourceLoader,
			ResourcePatternResolver resourcePatternResolver) {
		return new AmazonS3ClientWrapper(amazonS3, fredbetProperties.getBucket(), resourceLoader, resourcePatternResolver);
	}
}
