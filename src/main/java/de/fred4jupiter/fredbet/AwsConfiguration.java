package de.fred4jupiter.fredbet;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

@Configuration
@ImportResource("classpath:/aws-config.xml")
@ConditionalOnProperty(prefix = "fredbet", name = "image-location", havingValue = "aws-s3")
public class AwsConfiguration {

    
}
