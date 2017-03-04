package de.fred4jupiter.fredbet.autoconfigure;

import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.aws.context.config.annotation.EnableContextCredentials;
import org.springframework.cloud.aws.context.config.annotation.EnableContextResourceLoader;
import org.springframework.cloud.aws.context.support.io.ResourceLoaderBeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.ResourcePatternResolver;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;

import de.fred4jupiter.fredbet.FredbetProperties;
import de.fred4jupiter.fredbet.service.image.AmazonS3ClientWrapper;

@Configuration
@ConditionalOnProperty(prefix = "fredbet", name = "image-location", havingValue = "aws-s3")
@EnableContextResourceLoader
@EnableContextCredentials
public class AwsConfiguration {

    // @Bean
    // public static PropertySourcesPlaceholderConfigurer
    // propertySourcesPlaceholderConfigurer() {
    // return new PropertySourcesPlaceholderConfigurer();
    // }

    @Bean
    public AmazonS3 amazonS3Client(FredbetProperties fredbetProperties) {
        final String region = fredbetProperties.getRegion();
        if (StringUtils.isBlank(region)) {
            throw new IllegalStateException("AWS S3 is configured, but region value is empty!");
        }
        
        BasicAWSCredentials awsCredentials = new BasicAWSCredentials(fredbetProperties.getAccessKey(),
                fredbetProperties.getSecretAccessKey());
        AmazonS3Client amazonS3Client = new AmazonS3Client(awsCredentials);
        amazonS3Client.setRegion(Region.getRegion(Regions.fromName(region)));
        return amazonS3Client;
    }

    @Bean
    public ResourceLoaderBeanPostProcessor resourceLoaderBeanPostProcessor(AmazonS3 amazonS3) {
        return new ResourceLoaderBeanPostProcessor(amazonS3);
    }

    @Bean
    public AmazonS3ClientWrapper amazonS3ClientWrapper(AmazonS3 amazonS3, FredbetProperties fredbetProperties,
            ResourceLoader resourceLoader, ResourcePatternResolver resourcePatternResolver) {
        return new AmazonS3ClientWrapper(amazonS3, fredbetProperties.getBucket(), resourceLoader, resourcePatternResolver);
    }
}
